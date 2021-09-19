package com.martrust.zenko_cloudserver.bucket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martrust.zenko_cloudserver.object.GetObjDeleteMarkerResp;
import com.martrust.zenko_cloudserver.object.GetObjVersionedResp;
import com.martrust.zenko_cloudserver.object.ObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.*;

@Service
public class BucketService {


    @Autowired
    private ObjectService objectService;

    /**
     * todo: validation of bucket, limit, dir, error catching
     */
    public boolean getBucketVersioning(S3Client s3Client, String bucketName){
        try {
            GetBucketVersioningResponse bucketVersioning = s3Client.getBucketVersioning(GetBucketVersioningRequest.builder().bucket(bucketName).build());
            System.out.println(bucketVersioning);
            return  Objects.nonNull(bucketVersioning.status()) && "Enabled".equals(bucketVersioning.status().toString());
        }catch (S3Exception e){
            return false;
        }
    }


    public List<GetBucketResp> listBuckets(S3Client s3Client){

        List<GetBucketResp> bucketList = new ArrayList<>();

        s3Client.listBuckets().buckets().forEach(x ->{

            bucketList.add(
                    GetBucketResp.builder()
                    .bucketName(x.name())
                    .createdAt(x.creationDate().toString())
                    .isBucketEmpty(objectService.listPageableObjOnBucket(s3Client, x.name(), "",  "", 1).size() == 0)
                    .isBucketVersioned(this.getBucketVersioning(s3Client, x.name()))
                    .build());
        });
        return  bucketList;
    }

    /**
     * todo: validation of bucket, error catching
     * only existing bucket can be encrypted
     */
    public PutBucketEncryptionResponse encryptBucketDefaultSetting(S3Client s3Client, String bucketName){
        ServerSideEncryptionByDefault encryptionType = ServerSideEncryptionByDefault.builder().sseAlgorithm(ServerSideEncryption.AES256).build();
        ServerSideEncryptionRule encryptionRule = ServerSideEncryptionRule.builder().applyServerSideEncryptionByDefault(encryptionType).build();
        ServerSideEncryptionConfiguration encryptionConfig = ServerSideEncryptionConfiguration.builder().rules(encryptionRule).build();
        return s3Client.putBucketEncryption(PutBucketEncryptionRequest.builder().bucket(bucketName).serverSideEncryptionConfiguration(encryptionConfig).build());
    }


    /**
     * todo: validation of bucket, error catching
     * validate bucket name to be url safe because we will use it as queryParam
     */
    public boolean createBucket( S3Client s3Client, CreateBucketReq req) {

            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(req.getBucketName())
                    .build();

            CreateBucketResponse createBucketResponse = s3Client.createBucket(createBucketRequest);
            PutBucketEncryptionResponse putBucketEncryptionResponse = this.encryptBucketDefaultSetting(s3Client, req.getBucketName());

            if(req.getIsVersioned()){
                this.enableBucketVersioning(s3Client, req.getBucketName());
            }
            return putBucketEncryptionResponse.sdkHttpResponse().isSuccessful();
    }


    /**
     * todo: validation of bucket, error catching
     * need to check if bucket has object
     * in the future change to async since deleting large files can take much time
     */
    public boolean deleteBucket(S3Client s3Client, String bucketName){

        List<GetObjVersionedResp> objVersionedList = objectService.listPageableObjVersionOnBucket(s3Client, bucketName, "", 1000);
        List<GetObjDeleteMarkerResp> objDeleteMarkerList = objectService.listPageableObjDeleteMarkerOnBucket(s3Client, bucketName, "", 1000);

        // delete all object first
        while (!objVersionedList.isEmpty()){
            objVersionedList.forEach( x -> objectService.deleteVersionedFile(s3Client, bucketName, x.getKey(), x.getVersionId()));
            objVersionedList = objectService.listPageableObjVersionOnBucket(s3Client, bucketName, "", 1000);
        }

        // delete all object delete-marker first
        while (!objDeleteMarkerList.isEmpty()){
            objDeleteMarkerList.forEach( x -> objectService.deleteVersionedFile(s3Client, bucketName, x.getKey(), x.getVersionId()));
            objDeleteMarkerList = objectService.listPageableObjDeleteMarkerOnBucket(s3Client, bucketName, "", 1000);
        }

        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
        DeleteBucketResponse resp = s3Client.deleteBucket(deleteBucketRequest);
        return resp.sdkHttpResponse().isSuccessful();
    }


    /**
     * todo: validation of bucket, error catching
     * add req body to update versioning status to be able to revert to non version
     */
    public boolean enableBucketVersioning (S3Client s3Client, String bucketName){

        VersioningConfiguration versioningConfiguration = VersioningConfiguration.builder().status(BucketVersioningStatus.ENABLED).build();

        PutBucketVersioningRequest putBucketVersioningRequest = PutBucketVersioningRequest.builder().bucket(bucketName)
                .versioningConfiguration(versioningConfiguration)
                .build();

        PutBucketVersioningResponse resp = s3Client.putBucketVersioning(putBucketVersioningRequest);
        return resp.sdkHttpResponse().isSuccessful();
    }
}

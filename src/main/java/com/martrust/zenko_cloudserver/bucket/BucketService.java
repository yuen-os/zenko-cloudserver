package com.martrust.zenko_cloudserver.bucket;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BucketService {


    public List<Map<String, Object>> listBuckets(S3Client s3Client, Integer limit){
        List<Map<String, Object>> bucketList = new ArrayList<>();


        s3Client.listBuckets().buckets().forEach(x-> {

            try {

                GetBucketAclResponse bucketACL =  s3Client.getBucketAcl(GetBucketAclRequest.builder().bucket(x.name()).build());

                bucketACL.grants().forEach(y -> System.out.println(y.permissionAsString()));

                GetBucketVersioningResponse asd =  s3Client.getBucketVersioning(GetBucketVersioningRequest.builder().bucket(x.name()).build());

                ListObjectsRequest listObjects = ListObjectsRequest
                        .builder()
                        .bucket(x.name())
                        .build();

                ListObjectsResponse res =  s3Client.listObjects(listObjects);

                GetBucketEncryptionResponse getBucketEncryptionResponse = null;
                boolean isBucketEncrypted = true;
                try {
                    getBucketEncryptionResponse = s3Client.getBucketEncryption(GetBucketEncryptionRequest.builder().bucket(x.name()).build());

                }catch (Exception exception){
                    //cannot get sdkHttpResp since it throws s3 exception when not found
                    isBucketEncrypted = false;
                }

                bucketList.add(Map.of(

                        "createdAt", x.creationDate(),
                        "name", x.name() ,
                        "isBucketEmpty", !res.hasContents(),
                        "region", "sample",
                        "isBucketEncrypted", isBucketEncrypted,
                        "encryption",  isBucketEncrypted ? getBucketEncryptionResponse.serverSideEncryptionConfiguration().rules().stream().map(h -> h.applyServerSideEncryptionByDefault().sseAlgorithmAsString()): "none",
                //nullable resp of getversion of bucket
                        "isBucketVersioned" , Objects.isNull(asd.status()) ? false: true

                ));
            }catch (Exception e){
                e.printStackTrace();
            }

        } );
        return bucketList;
    }


    // validate bucket name to be url safe because we will use it as queryParam
    public boolean createBucket( S3Client s3Client, String bucketName) {

        try {

            S3Waiter s3Waiter = s3Client.waiter();
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)

                    .build();

            s3Client.createBucket(bucketRequest);
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();


            // Wait until the bucket is created and print out the response
            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
            s3Client.putBucketEncryption(PutBucketEncryptionRequest.builder().bucket(bucketName).serverSideEncryptionConfiguration(ServerSideEncryptionConfiguration.builder().rules(ServerSideEncryptionRule.builder().applyServerSideEncryptionByDefault(ServerSideEncryptionByDefault.builder().sseAlgorithm(ServerSideEncryption.AES256).build()).build()).build()).build());

            return waiterResponse.matched().response().isPresent();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            return  false;
        }
    }


    // need to check acl, need to check if bucket has object
    // in the future change to async since deleting large files can take much time
    public boolean deleteBucket(S3Client s3Client, String bucketName){
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
        DeleteBucketResponse resp = s3Client.deleteBucket(deleteBucketRequest);
        return resp.sdkHttpResponse().isSuccessful();
    }


    /**
     * todo add req body to update versioning status
     * @param s3Client
     * @param bucketName
     * @return
     */
    public boolean enableBucketVersioning (S3Client s3Client, String bucketName){

        PutBucketVersioningRequest putBucketVersioningRequest = PutBucketVersioningRequest.builder().bucket(bucketName)
                .versioningConfiguration(VersioningConfiguration.builder().status(BucketVersioningStatus.ENABLED).build())
                .build();
        PutBucketVersioningResponse resp = s3Client.putBucketVersioning(putBucketVersioningRequest);
        return resp.sdkHttpResponse().isSuccessful();
    }
}

package com.martrust.zenko_cloudserver.object;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class ObjectService {

    /**
     * todo: validation of bucket, limit, error catching
     * even if the startAtKey is invalid still don't throw error
     * https://stackoverflow.com/questions/59203291/aws-s3-sdk-listobjectsv2-whats-difference-between-startafter-and-continuationto
     */
    public List<GetObjResp> listPageableObjOnBucket(S3Client s3Client, String bucketName,  String prefix,  String startAfterKey, Integer limit) {

        List<GetObjResp> objList = new ArrayList<>();
        String lastObjKey;
        ListObjectsV2Request req = ListObjectsV2Request.builder()
                .bucket(bucketName).maxKeys(limit).prefix(prefix)
                .startAfter(startAfterKey).fetchOwner(true).build();
        ListObjectsV2Response objects =  s3Client.listObjectsV2(req);

        if(objects.hasContents()){
            lastObjKey = objects.contents().get(objects.contents().size()-1 ).key();
            objects.contents().forEach(x->{

                objList.add(
                        GetObjResp.builder()
                                .key(x.key())
                                .size(x.size()/1024 + " KBs")
                                .ownerName( x.owner().displayName())
                                .ownerId(x.owner().id())
                                .etag(x.eTag())
                                .storageClass(x.storageClassAsString())
                                .lastModifiedDate(x.lastModified().toString())
                                .lastObjKey(lastObjKey)
                                .build());
            });
        }
        return objList;
    }


    /**
     * todo: validation of bucket, limit, error catching
     * doesn't show on cyberduck console the versions, only the latest
     * will not get error even if the bucket is not version
     * this will only show the previous version not the delete marker
     */
        public List<GetObjVersionedResp> listPageableObjVersionOnBucket(S3Client s3Client, String bucketName, String prefix, Integer limit) {

            List<GetObjVersionedResp> objVersionedList = new ArrayList<>();
            ListObjectVersionsResponse objects =  s3Client.listObjectVersions(ListObjectVersionsRequest.builder()
                .bucket(bucketName).maxKeys(limit).prefix(prefix).build());

            if(!objects.versions().isEmpty()){

                objects.versions().forEach(x-> {
                    objVersionedList.add(
                            GetObjVersionedResp.builder()
                            .key(x.key())
                            .size(x.size()/1024 + " KBs")
                            .versionId(x.versionId())
                            .ownerName(x.owner().displayName())
                            .ownerId(x.owner().id())
                            .isLatest(x.isLatest())
                            .etag(x.eTag())
                            .storageClass(x.storageClassAsString())
                            .lastModifiedDate(x.lastModified().toString())
                            .build());
                });

            }
        return objVersionedList;

    }

    /**
     * todo: validation of bucket, limit, error catching
     * this will only show the latest version delete marker
     * if you want to restore an object just delete the latest
     * delete marker on the delete version endpoint
     */
    public List<GetObjDeleteMarkerResp> listPageableObjDeleteMarkerOnBucket(S3Client s3Client, String bucketName, String prefix, Integer limit) {

        List<GetObjDeleteMarkerResp> objDeleteMarkerList = new ArrayList<>();
        ListObjectVersionsResponse objects =  s3Client.listObjectVersions(ListObjectVersionsRequest.builder()
                .bucket(bucketName).maxKeys(limit).prefix(prefix).build());

        if(!objects.versions().isEmpty() || !objects.deleteMarkers().isEmpty()){
            objects.deleteMarkers().forEach(x->{
                objDeleteMarkerList.add(
                        GetObjDeleteMarkerResp.builder()
                        .key(x.key())
                        .versionId(x.versionId())
                        .ownerName(x.owner().displayName())
                        .ownerId(x.owner().id())
                        .isLatest(x.isLatest())
                        .lastModifiedDate(x.lastModified().toString())
                        .build());
            });
        }
        return objDeleteMarkerList;
    }

    /**
     * todo: validation of bucket, limit, error catching
     */
    public String generatePresignedUrl(S3Presigner s3Presigner, String bucketName, String keyName ) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                            .bucket(bucketName).key(keyName).build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(7))
                    .getObjectRequest(getObjectRequest).build();

            PresignedGetObjectRequest presignedGetObjectRequest =
                    s3Presigner.presignGetObject(getObjectPresignRequest);

            return presignedGetObjectRequest.url().toString();

    }

    /**
     * todo: validation of bucket, limit, error catching, authorization before deleting (business rule)
     */
    public boolean deleteFile(S3Client s3Client, String bucketName, String key){

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        DeleteObjectResponse delObjResp = s3Client.deleteObject(deleteObjectRequest);
        return delObjResp.sdkHttpResponse().isSuccessful();
    }


    /**
     * todo: validation of bucket, limit, error catching, authorization before deleting (business rule)
     */
    public boolean deleteVersionedFile(S3Client s3Client, String bucketName, String key, String versionId){

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName).versionId(versionId)
                .key(key).build();

        DeleteObjectResponse delObjResp = s3Client.deleteObject(deleteObjectRequest);
        return delObjResp.sdkHttpResponse().isSuccessful();
    }

    /**
     * todo: validation of bucket, limit, dir, error catching, authorization before deleting (business rule)
     */
    public boolean uploadFile(S3Client s3Client, String bucketName, String dir, MultipartFile file) throws IOException {

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .serverSideEncryption(ServerSideEncryption.AES256)
                .key(dir.concat(file.getOriginalFilename()))
                .build();

        PutObjectResponse resp =  s3Client.putObject(objectRequest, RequestBody.fromBytes(file.getBytes()));
        return  resp.sdkHttpResponse().isSuccessful();
    }

}

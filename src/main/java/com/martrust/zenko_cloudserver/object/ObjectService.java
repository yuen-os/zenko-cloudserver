package com.martrust.zenko_cloudserver.object;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Map;

@Service
public class ObjectService {


    public List<Map<String,String>> listPageableObjOnBucket(S3Client s3Client, String bucketName, Integer limit) {

        List<Map<String,String>> objectList = new ArrayList<>();
        boolean hasNext;
        String lastObjKey = "";
        ListObjectsV2Request req = ListObjectsV2Request.builder().bucket(bucketName).maxKeys(limit).fetchOwner(true).build();

        do{

            ListObjectsV2Response objects =  s3Client.listObjectsV2(req);
            hasNext = objects.isTruncated();

            if(objects.hasContents()){

                lastObjKey =   objects.contents().get(objects.contents().size()-1 ).key();

                objects.contents().forEach(x->{

                    objectList.add(Map.of(
                            "key", x.key(),
                            "size", x.size()/1024 + " KBs",
                            "ownerName", x.owner().displayName(),
                            "ownerId", x.owner().id(),
                            "etag", x.eTag(),
                            "storageClass", x.storageClassAsString(),
                            "lastModified", x.lastModified().toString()
                    ));
                });

            }

            req = ListObjectsV2Request.builder().bucket(bucketName).startAfter(lastObjKey).fetchOwner(true).maxKeys(limit).build();

        }while (hasNext);

        return objectList;

    }

    public String generatePresignedUrl(S3Presigner s3Presigner, String bucketName, String keyName ) {

        try {
            GetObjectRequest getObjectRequest =
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(keyName)
                            .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(7))
                    .getObjectRequest(getObjectRequest)
                    .build();

            // Generate the presigned request
            PresignedGetObjectRequest presignedGetObjectRequest =
                    s3Presigner.presignGetObject(getObjectPresignRequest);

            return presignedGetObjectRequest.url().toString();

        } catch (S3Exception e) {
            e.getStackTrace();
            return null;
        }

    }

    public boolean deleteFile(S3Client s3Client, String bucketName, String key){
        //do security check because they can delete files that are on different directory so check acl
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        DeleteObjectResponse delObjResp = s3Client.deleteObject(deleteObjectRequest);
        return delObjResp.sdkHttpResponse().isSuccessful();
    }

    public boolean uploadFile(S3Client s3Client, String bucketName, String dir, MultipartFile file) throws IOException {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(dir.concat(file.getOriginalFilename()))
                .build();

        PutObjectResponse resp =  s3Client.putObject(objectRequest, RequestBody.fromBytes(file.getBytes()));

        return  resp.sdkHttpResponse().isSuccessful();
    }

}

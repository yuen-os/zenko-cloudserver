package com.martrust.zenko_cloudserver.bucket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetBucketResp {
    private String bucketName;
    //there's no sense of using multi region on a localfile storage
    private final String bucketRegion = "us-east-1";
    private String createdAt;
    //since zenko don't support aws-kms on local file storage we have to hard code this
    private final String bucketEncryption = ServerSideEncryption.AES256.toString();
    private Boolean isBucketEmpty;
    private Boolean isBucketVersioned;
}

package com.martrust.zenko_cloudserver.bucket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBucketReq {
    private String bucketName;
    private Boolean isVersioned = false;
}

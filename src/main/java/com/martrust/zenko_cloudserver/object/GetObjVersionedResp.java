package com.martrust.zenko_cloudserver.object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetObjVersionedResp {
    private String key;
    private String size;
    private String versionId;
    private String ownerName;
    private String ownerId;
    private String etag;
    private String storageClass;
    private String lastModifiedDate;
    private Boolean isLatest;
}

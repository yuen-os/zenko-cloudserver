package com.martrust.zenko_cloudserver.object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetObjDeleteMarkerResp {
    private String key;
    private String versionId;
    private String ownerName;
    private String ownerId;
    private String lastModifiedDate;
    private Boolean isLatest;
}

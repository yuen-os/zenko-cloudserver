package com.martrust.zenko_cloudserver.bucket;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;
import java.util.Map;

@Tag(name = "Bucket Resource", description = "Endpoints for managing buckets")
@RequestMapping("/bucket")
@RestController
public class BucketController {

    private final S3Client s3Client;
    private final BucketService bucketService;

    public BucketController(S3Client s3Client, BucketService bucketService) {
        this.s3Client = s3Client;
        this.bucketService = bucketService;
    }


    @Operation(summary  = "list buckets")
    @GetMapping
    public ResponseEntity<Map<String,List<GetBucketResp>>> listBuckets(){
         return ResponseEntity.status(200).body(Map.of("content", bucketService.listBuckets(s3Client)));
    }


    @Operation(summary  = "create bucket")
    @PostMapping
    public ResponseEntity<Map<String,Object>> createBucket(@RequestBody CreateBucketReq req){
        return ResponseEntity.status(200).body(Map.of("content", bucketService.createBucket(s3Client, req)));
    }


    @Operation(summary  = "delete bucket")
    @DeleteMapping("/{bucketName}")
    public ResponseEntity<Map<String,Object>> deleteBucket(@PathVariable(name = "bucketName") String bucketName ){
        return ResponseEntity.status(200).body(Map.of("content", bucketService.deleteBucket(s3Client, bucketName)));
    }

    @Operation(summary  = "update bucket version")
    @PutMapping("/{bucketName}/version")
    public ResponseEntity<Map<String,Object>> updateBucketVersioning(@PathVariable(name = "bucketName") String bucketName ){
        return ResponseEntity.status(200).body(Map.of("content", bucketService.enableBucketVersioning(s3Client, bucketName)));
    }

}

package com.martrust.zenko_cloudserver.bucket;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;

@Api(tags = "Bucket Resource")
@RequestMapping("/api/bucket")
@RestController
public class BucketController {

    private S3Client s3Client;
    private BucketService bucketService;

    public BucketController(S3Client s3Client, BucketService bucketService) {
        this.s3Client = s3Client;
        this.bucketService = bucketService;
    }


    @ApiResponse(responseCode = "200", description = "list buckets")
    @GetMapping
    public ResponseEntity<List<GetBucketResp>> listBuckets(){
         return ResponseEntity.status(200).body(bucketService.listBuckets(s3Client));
    }


    @ApiResponse(responseCode = "200", description = "create bucket")
    @PostMapping
    public ResponseEntity<Boolean> createBucket(@RequestBody CreateBucketReq req){
        return ResponseEntity.status(200).body(bucketService.createBucket(s3Client, req));
    }


    @ApiResponse(responseCode = "200", description = "delete bucket")
    @DeleteMapping("/{bucketName}")
    public ResponseEntity<Boolean> deleteBucket(@PathVariable(name = "bucketName") String bucketName ){
        return ResponseEntity.status(200).body(bucketService.deleteBucket(s3Client, bucketName));
    }

    @ApiResponse(responseCode = "200", description = "update bucket version")
    @PutMapping("/{bucketName}/version")
    public ResponseEntity<Boolean> updateBucketVersioning(@PathVariable(name = "bucketName") String bucketName ){
        return ResponseEntity.status(200).body(bucketService.enableBucketVersioning(s3Client, bucketName));
    }

}

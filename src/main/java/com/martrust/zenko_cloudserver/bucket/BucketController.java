package com.martrust.zenko_cloudserver.bucket;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.S3Client;

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


    @ApiOperation(value = "list buckets", response = GetBucketResp.class, responseContainer = "List")
    @GetMapping
    public ResponseEntity listBuckets(){
         return ResponseEntity.status(200).body(bucketService.listBuckets(s3Client));
    }


    @ApiOperation(value = "create bucket")
    @PostMapping
    public ResponseEntity createBucket(@RequestBody CreateBucketReq req){
        return ResponseEntity.status(200).body(bucketService.createBucket(s3Client, req));
    }


    @ApiOperation(value = "delete bucket")
    @DeleteMapping("/{bucketName}")
    public ResponseEntity deleteBucket(@PathVariable(name = "bucketName") String bucketName ){
        return ResponseEntity.status(200).body(bucketService.deleteBucket(s3Client, bucketName));
    }

    @ApiOperation(value = "update bucket version")
    @PutMapping("/{bucketName}/version")
    public ResponseEntity updateBucketVersioning(@PathVariable(name = "bucketName") String bucketName ){
        return ResponseEntity.status(200).body(bucketService.enableBucketVersioning(s3Client, bucketName));
    }

}

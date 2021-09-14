package com.martrust.zenko_cloudserver.bucket;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.*;

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

    @ApiOperation(value = "list buckets")
    @GetMapping
    public ResponseEntity listBuckets(@RequestParam(name = "limit", defaultValue = "1000") Integer limit){
         return ResponseEntity.status(200).body(bucketService.listBuckets(s3Client, limit));
    }


    @ApiOperation(value = "create bucket")
    @PostMapping
    public ResponseEntity createBucket(@RequestBody Map<String, String> req){
        return ResponseEntity.status(200).body(bucketService.createBucket(s3Client, req.get("bucketName")));
    }


    @ApiOperation(value = "delete bucket")
    @DeleteMapping("/{bucketName}")
    public ResponseEntity deleteBucket(@PathVariable(name = "bucketName") String bucketName ){
        return ResponseEntity.status(200).body(bucketService.deleteBucket(s3Client, bucketName));
    }

}

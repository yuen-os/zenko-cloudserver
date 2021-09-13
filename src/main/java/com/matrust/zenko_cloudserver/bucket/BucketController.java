package com.matrust.zenko_cloudserver.bucket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import software.amazon.awssdk.utils.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.util.*;

@RequestMapping("/api/bucket")
@RestController
public class BucketController {

    private S3Client s3Client;
    private BucketService bucketService;

    public BucketController(S3Client s3Client, BucketService bucketService) {
        this.s3Client = s3Client;
        this.bucketService = bucketService;
    }

    @GetMapping
    public ResponseEntity listBuckets(@RequestParam(name = "limit", defaultValue = "1000") Integer limit){
         return ResponseEntity.status(200).body(bucketService.listBuckets(s3Client, limit));
    }


    @PostMapping
    public ResponseEntity createBucket(@RequestBody Map<String, String> req){
        return ResponseEntity.status(200).body(bucketService.createBucket(s3Client, req.get("bucketName")));
    }


    @DeleteMapping("/{bucketName}")
    public ResponseEntity deleteBucket(@PathVariable(name = "bucketName") String bucketName ){
        return ResponseEntity.status(200).body(bucketService.deleteBucket(s3Client, bucketName));
    }

}

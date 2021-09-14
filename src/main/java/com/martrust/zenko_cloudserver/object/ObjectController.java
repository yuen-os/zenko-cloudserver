package com.martrust.zenko_cloudserver.object;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;
import java.util.Map;

@Api(tags = "Object Resource")
@RestController("/api/bucket")
public class ObjectController {

    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private ObjectService objectService;

    public ObjectController(S3Client s3Client, S3Presigner s3Presigner, ObjectService objectService) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.objectService = objectService;
    }


    @ApiOperation(value = "list objects")
    @GetMapping("/{bucketName}/file")
    public ResponseEntity listPageableObjOnBucket(@PathVariable(name = "bucketName") String bucketName, @RequestParam(name = "limit", defaultValue = "1000") Integer limit ) {
        return ResponseEntity.status(200).body(objectService.listPageableObjOnBucket(s3Client, bucketName, limit));
    }


    @ApiOperation(value = "upload file")
    @PostMapping("/{bucketName}/file")
    public ResponseEntity uploadFile(@PathVariable(name = "bucketName") String bucketName, @RequestParam("file") MultipartFile file, @RequestParam("dir") String dir) throws IOException {
        return ResponseEntity.status(200).body(objectService.uploadFile(s3Client, bucketName, dir, file));
    }

    @ApiOperation(value = "generate signed url")
    @PostMapping("/{bucketName}/file-url")
    public ResponseEntity generatePresignedUrl(@PathVariable(name = "bucketName") String bucketName, @RequestBody Map<String, String> req) {
        return ResponseEntity.status(200).body(objectService.generatePresignedUrl( s3Presigner,  bucketName, req.get("fileName")));
    }

    @ApiOperation(value = "delete file")
    @DeleteMapping("/{bucketName}/file")
    public ResponseEntity deleteFile(@PathVariable(name = "bucketName") String bucketName, @RequestBody Map<String, String> req) {
        return ResponseEntity.status(200).body(objectService.deleteFile( s3Client, bucketName, req.get("fileName")));
    }


}

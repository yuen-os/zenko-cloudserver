package com.martrust.zenko_cloudserver.object;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Api(tags = "Object Resource")
@RestController
@RequestMapping("/api/bucket")
public class ObjectController {

    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private ObjectService objectService;

    public ObjectController(S3Client s3Client, S3Presigner s3Presigner, ObjectService objectService) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.objectService = objectService;
    }


    @ApiResponse(responseCode = "200", description = "list objects on bucket")
    @ApiOperation(value = "list objects")
    @GetMapping("/{bucketName}/file")
    public ResponseEntity<List<GetObjResp>> listPageableObjOnBucket(@PathVariable(name = "bucketName") String bucketName, @RequestParam(name = "limit", defaultValue = "1000") Integer limit,
                                                                    @RequestParam(name = "startAfterKey", defaultValue = "") String startAfterKey,
                                                                    @RequestParam(name = "prefix", defaultValue = "") String prefix) {
        return ResponseEntity.status(200).body(objectService.listPageableObjOnBucket(s3Client, bucketName, prefix, startAfterKey, limit));
    }



    @ApiResponse(responseCode = "200", description = "list objects without delete marker")
    @GetMapping("/{bucketName}/file/version")
    public ResponseEntity listPageableObjVersionOnBucket(@PathVariable(name = "bucketName") String bucketName, @RequestParam(name = "limit", defaultValue = "1000") Integer limit,
                                                         @RequestParam(name = "prefix", defaultValue = "") String prefix) {
        return ResponseEntity.status(200).body(objectService.listPageableObjVersionOnBucket(s3Client, bucketName, prefix, limit));
    }


    @ApiResponse(responseCode = "200", description = "list objects and the delete marker")
    @GetMapping("/{bucketName}/file/version/delete-marker")
    public ResponseEntity<List<GetObjDeleteMarkerResp>> listPageableObjDeleteMarkerOnBucket(@PathVariable(name = "bucketName") String bucketName, @RequestParam(name = "limit", defaultValue = "1000") Integer limit,
                                                              @RequestParam(name = "prefix", defaultValue = "") String prefix) {
        return ResponseEntity.status(200).body(objectService.listPageableObjDeleteMarkerOnBucket(s3Client, bucketName, prefix, limit));
    }

    @ApiResponse(responseCode = "200", description = "upload file")
    @PostMapping("/{bucketName}/file")
    public ResponseEntity<Boolean> uploadFile(@PathVariable(name = "bucketName") String bucketName, @RequestParam("file") MultipartFile file, @RequestParam("dir") String dir) throws IOException {
        return ResponseEntity.status(200).body(objectService.uploadFile(s3Client, bucketName, dir, file));
    }


    @ApiResponse(responseCode = "200", description = "generate signed url")
    @PostMapping( "/{bucketName}/file-url")
    public ResponseEntity<String> generatePresignedUrl(@PathVariable(name = "bucketName") String bucketName,
                                               @ApiParam(example =  "{\"fileName\" : \"string\"}", required = true)
                                               @RequestBody  Map<String, String> req) {
        return ResponseEntity.status(200).body(objectService.generatePresignedUrl( s3Presigner,  bucketName, req.get("fileName")));
    }

    @ApiResponse(responseCode = "200", description = "delete file")
    @DeleteMapping("/{bucketName}/file")
    public ResponseEntity<Boolean> deleteFile(@PathVariable(name = "bucketName") String bucketName, @RequestBody Map<String, String> req) {
        return ResponseEntity.status(200).body(objectService.deleteFile( s3Client, bucketName, req.get("fileName")));
    }


    @ApiResponse(responseCode = "200", description = "delete version file")
    @DeleteMapping("/{bucketName}/file/version")
    public ResponseEntity<Boolean>deleteVersionFile(@PathVariable(name = "bucketName") String bucketName, @RequestBody Map<String, String> req) {
        return ResponseEntity.status(200).body(objectService.deleteVersionedFile( s3Client, bucketName, req.get("fileName"), req.get("versionId") ));
    }


}

package com.martrust.zenko_cloudserver.object;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Tag(name = "Object Resource", description = "Endpoints for managing files/objects")
@RestController
@RequestMapping("/bucket")
public class ObjectController {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final ObjectService objectService;

    public ObjectController(S3Client s3Client, S3Presigner s3Presigner, ObjectService objectService) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.objectService = objectService;
    }


    @Operation(summary  = "list objects on bucket")
    @GetMapping("/{bucketName}/file")
    public ResponseEntity<Map<String, List<GetObjResp>>> listPageableObjOnBucket(@PathVariable(name = "bucketName") String bucketName, @RequestParam(name = "limit", defaultValue = "1000") Integer limit,
                                                                    @RequestParam(name = "startAfterKey", defaultValue = "") String startAfterKey,
                                                                    @RequestParam(name = "prefix", defaultValue = "") String prefix) {
        return ResponseEntity.status(200).body(Map.of("content", objectService.listPageableObjOnBucket(s3Client, bucketName, prefix, startAfterKey, limit)));
    }



    @Operation(summary  = "list objects without delete marker")
    @GetMapping("/{bucketName}/file/version")
    public ResponseEntity<Map<String, List<GetObjVersionedResp>>> listPageableObjVersionOnBucket(@PathVariable(name = "bucketName") String bucketName, @RequestParam(name = "limit", defaultValue = "1000") Integer limit,
                                                         @RequestParam(name = "prefix", defaultValue = "") String prefix) {
        return ResponseEntity.status(200).body(Map.of("content", objectService.listPageableObjVersionOnBucket(s3Client, bucketName, prefix, limit)));
    }


    @Operation(summary  = "list objects and the delete marker")
    @GetMapping("/{bucketName}/file/version/delete-marker")
    public ResponseEntity<Map<String, List<GetObjDeleteMarkerResp>>> listPageableObjDeleteMarkerOnBucket(@PathVariable(name = "bucketName") String bucketName, @RequestParam(name = "limit", defaultValue = "1000") Integer limit,
                                                              @RequestParam(name = "prefix", defaultValue = "") String prefix) {
        return ResponseEntity.status(200).body(Map.of("content", objectService.listPageableObjDeleteMarkerOnBucket(s3Client, bucketName, prefix, limit)));
    }

    @Operation(summary  = "upload file")
    @PostMapping("/{bucketName}/file")
    public ResponseEntity<Map<String,Object>> uploadFile(@PathVariable(name = "bucketName") String bucketName, @RequestParam("file") MultipartFile file, @RequestParam("dir") String dir) throws IOException {
        return ResponseEntity.status(200).body(Map.of("content", objectService.uploadFile(s3Client, bucketName, dir, file)));
    }


    @Operation(summary  = "generate signed url")
    @PostMapping( "/{bucketName}/file-url")
    public ResponseEntity<Map<String, Object>> generatePresignedUrl(@PathVariable(name = "bucketName") String bucketName,
                                               @RequestBody  Map<String, String> req) {
        return ResponseEntity.status(200).body(Map.of("content", objectService.generatePresignedUrl( s3Presigner,  bucketName, req.get("fileName"))));
    }

    @Operation(summary  = "delete file")
    @DeleteMapping("/{bucketName}/file")
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable(name = "bucketName") String bucketName, @RequestBody Map<String, String> req) {
        return ResponseEntity.status(200).body(Map.of("content", objectService.deleteFile( s3Client, bucketName, req.get("fileName"))));
    }


    @Operation(summary  = "delete version file")
    @DeleteMapping("/{bucketName}/file/version")
    public ResponseEntity<Map<String,Object>>deleteVersionFile(@PathVariable(name = "bucketName") String bucketName, @RequestBody Map<String, String> req) {
        return ResponseEntity.status(200).body( Map.of("content", objectService.deleteVersionedFile( s3Client, bucketName, req.get("fileName"), req.get("versionId") )));
    }


}

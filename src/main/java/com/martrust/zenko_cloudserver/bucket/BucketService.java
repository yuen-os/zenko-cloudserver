package com.martrust.zenko_cloudserver.bucket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.martrust.zenko_cloudserver.object.ObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.*;

@Service
public class BucketService {


    @Autowired
    private ObjectService objectService;

    /**
     * todo: validation of bucket, limit, dir, error catching
     */
    public boolean getBucketVersioning(S3Client s3Client, String bucketName){
        try {
            GetBucketVersioningResponse bucketVersioning = s3Client.getBucketVersioning(GetBucketVersioningRequest.builder().bucket(bucketName).build());
            System.out.println(bucketVersioning);
            return  Objects.nonNull(bucketVersioning.status()) && "Enabled".equals(bucketVersioning.status().toString());
        }catch (S3Exception e){
            return false;
        }
    }


    public List<GetBucketResp> listBuckets(S3Client s3Client){

        List<GetBucketResp> bucketList = new ArrayList<>();

        s3Client.listBuckets().buckets().forEach(x ->{

            bucketList.add(
                    GetBucketResp.builder()
                    .bucketName(x.name())
                    .createdAt(x.creationDate().toString())
                    .isBucketEmpty(objectService.listPageableObjOnBucket(s3Client, x.name(), "",  "", 1).size() == 0)
                    .isBucketVersioned(this.getBucketVersioning(s3Client, x.name()))
                    .build());
        });
        return  bucketList;
    }

    /**
     * todo: validation of bucket, error catching
     * only existing bucket can be encrypted
     */
    public PutBucketEncryptionResponse encryptBucketDefaultSetting(S3Client s3Client, String bucketName){
        ServerSideEncryptionByDefault encryptionType = ServerSideEncryptionByDefault.builder().sseAlgorithm(ServerSideEncryption.AES256).build();
        ServerSideEncryptionRule encryptionRule = ServerSideEncryptionRule.builder().applyServerSideEncryptionByDefault(encryptionType).build();
        ServerSideEncryptionConfiguration encryptionConfig = ServerSideEncryptionConfiguration.builder().rules(encryptionRule).build();
        return s3Client.putBucketEncryption(PutBucketEncryptionRequest.builder().bucket(bucketName).serverSideEncryptionConfiguration(encryptionConfig).build());
    }


    /**
     * todo: validation of bucket, error catching
     * validate bucket name to be url safe because we will use it as queryParam
     */
    public boolean createBucket( S3Client s3Client, CreateBucketReq req) {

            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(req.getBucketName())
                    .build();

            CreateBucketResponse createBucketResponse = s3Client.createBucket(createBucketRequest);
            PutBucketEncryptionResponse putBucketEncryptionResponse = this.encryptBucketDefaultSetting(s3Client, req.getBucketName());

            if(req.getIsVersioned()){
                this.enableBucketVersioning(s3Client, req.getBucketName());
            }
            return putBucketEncryptionResponse.sdkHttpResponse().isSuccessful();
    }


    /**
     * todo: validation of bucket, error catching
     * need to check if bucket has object
     * in the future change to async since deleting large files can take much time
     */
    public boolean deleteBucket(S3Client s3Client, String bucketName){
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
        DeleteBucketResponse resp = s3Client.deleteBucket(deleteBucketRequest);
        return resp.sdkHttpResponse().isSuccessful();
    }


    /**
     * todo: validation of bucket, error catching
     * add req body to update versioning status to be able to revert to non version
     */
    public boolean enableBucketVersioning (S3Client s3Client, String bucketName){

        VersioningConfiguration versioningConfiguration = VersioningConfiguration.builder().status(BucketVersioningStatus.ENABLED).build();

        PutBucketVersioningRequest putBucketVersioningRequest = PutBucketVersioningRequest.builder().bucket(bucketName)
                .versioningConfiguration(versioningConfiguration)
                .build();

        PutBucketVersioningResponse resp = s3Client.putBucketVersioning(putBucketVersioningRequest);
        return resp.sdkHttpResponse().isSuccessful();
    }



    /*************** EXPERIMENTAL ***************/

    public boolean addBucketLifeCycleRule(S3Client s3Client, String bucketName){
        Transition transition = Transition.builder()
                .storageClass("us-east-2")
//                .date(Instant.now().plusSeconds(TimeUnit.MINUTES.toSeconds(5) ))
                .days(6)
//                .date(Instant.now().plusSeconds(TimeUnit.DAYS.toSeconds(30) ))
                .build();

        LifecycleRule lifecycleRule =  LifecycleRule.builder()
                .status(ExpirationStatus.ENABLED)
                .id("transition_to_next_buck")
                .filter(LifecycleRuleFilter.builder().prefix("/").build())
                .transitions(transition)
                .build();

        BucketLifecycleConfiguration bucketLifecycleConfiguration =  BucketLifecycleConfiguration.builder().rules(lifecycleRule).build();

        PutBucketLifecycleConfigurationRequest putBucketLifecycleConfigurationRequest = PutBucketLifecycleConfigurationRequest.builder().lifecycleConfiguration(bucketLifecycleConfiguration).bucket(bucketName).build();


        PutBucketLifecycleConfigurationResponse asd = s3Client.putBucketLifecycleConfiguration(putBucketLifecycleConfigurationRequest);
        System.out.println(asd.toString());
        System.out.println(bucketName);
        System.out.println(asd.sdkHttpResponse().statusCode());
        System.out.println(asd.sdkHttpResponse().statusText().get());
        return asd.sdkHttpResponse().isSuccessful();
    }

//    Validate that destination bucket exists and has versioning
    //https://zenko.readthedocs.io/en/latest/reference/command_reference/s3api_commands/put-bucket-replication.html
    public boolean addBucketReplication(S3Client s3Client, String bucketName){


        String iamRoleArn = "arn:aws:iam::123456789012:root";
//        String iamRoleArn = "arn:aws:iam::123456789012:role/s3-replication-role";

//        String replicationId = "sample_replication_from_to_bucket";
        ReplicationRuleFilter replicationRuleFilter = ReplicationRuleFilter.builder().prefix("").build();
        Destination bucketDestination = Destination.builder().bucket("second-bucket")
                .storageClass(StorageClass.STANDARD)
                .build();

        DeleteMarkerReplication deleteMarkerReplication = DeleteMarkerReplication.builder().status(DeleteMarkerReplicationStatus.ENABLED).build();
        ExistingObjectReplication existingObjectReplication = ExistingObjectReplication.builder().status(ExistingObjectReplicationStatus.ENABLED).build();

        ReplicationRule replicationRule = ReplicationRule.builder()
//                .id(replicationId)
                .destination(bucketDestination)
                .status(ReplicationRuleStatus.ENABLED)
                .priority(1).deleteMarkerReplication(deleteMarkerReplication)
                .existingObjectReplication(existingObjectReplication)
                .filter(replicationRuleFilter).build();

        ReplicationConfiguration replicationConfiguration = ReplicationConfiguration.builder() .role(iamRoleArn).rules(replicationRule).build();

        PutBucketReplicationRequest putBucketReplicationRequest = PutBucketReplicationRequest.builder().bucket(bucketName).replicationConfiguration(replicationConfiguration).build();

        PutBucketReplicationResponse response = s3Client.putBucketReplication(putBucketReplicationRequest);

        System.out.println(response.toString());
        System.out.println(bucketName);
        System.out.println(response.sdkHttpResponse().statusCode());
        System.out.println(response.sdkHttpResponse().statusText().get());
        return response.sdkHttpResponse().isSuccessful();
    }

//    https://awspolicygen.s3.amazonaws.com/policygen.html
//    https://awscli.amazonaws.com/v2/documentation/api/latest/reference/s3api/get-bucket-policy.html
    public boolean addBucketPolicy(S3Client s3Client, String bucketName)  {
//todo work on json policy
        try {


            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> mainMap = new HashMap<>();
            mainMap.put("Id", "Policy1631767102478");
            mainMap.put("Version", "2012-10-17");




            Map<String, Object> statementMap = new HashMap<>();
            statementMap.put("Sid", "Stmt1631767101042");
            statementMap.put("Action", "s3:*");
            statementMap.put("Effect", "Allow");
            statementMap.put("Resource", "arn:aws:s3:::second-bucket/*");
            statementMap.put("Principal", "*");

            mainMap.put("Statement", Arrays.asList(statementMap));

            String jsonPolicy = mapper.writeValueAsString(mainMap);

            System.out.println(jsonPolicy);

            PutBucketPolicyRequest putBucketPolicyRequest = PutBucketPolicyRequest.builder().bucket(bucketName)
                    .policy(jsonPolicy)
                    .build();

            PutBucketPolicyResponse response = s3Client.putBucketPolicy(putBucketPolicyRequest);

            System.out.println(response.toString());
            System.out.println(bucketName);
            System.out.println(response.sdkHttpResponse().statusCode());
            System.out.println(response.sdkHttpResponse().statusText().get());
            return response.sdkHttpResponse().isSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }




}

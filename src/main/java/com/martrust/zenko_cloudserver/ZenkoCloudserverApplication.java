package com.martrust.zenko_cloudserver;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * all sdk methods
 * https://docs.aws.amazon.com/AmazonS3/latest/API/API_Operations_Amazon_Simple_Storage_Service.html
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-s3.html
 * https://mvnrepository.com/artifact/software.amazon.awssdk/s3/2.17.35
 *
 * endpoint override
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/region-selection.html
 *
 * https://docs.aws.amazon.com/en_us/AmazonS3/latest/API/API_HeadBucket.html
 *
 * https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-keys.html
 *
 * https://docs.amazonaws.cn/en_us/AmazonS3/latest/userguide/list-obj-version-enabled-bucket.html
 *
 * https://www.tabnine.com/code/java/methods/software.amazon.awssdk.services.s3.model.ListObjectsV2Request/builder
 *  https://stackoverflow.com/questions/67898208/aws-sdk-v2-s3-fetch-object-is-not-fetching-objects-more-than-1000
 *
 *  https://github.com/aws/aws-sdk-java-v2/blob/c9c33fb57a414f5417e6607f7f5e0d27cae37423/docs/LaunchChangelog.md#411-s3-operation-migration
 *
 */
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Cloudserver", version = "1.0"))
public class ZenkoCloudserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZenkoCloudserverApplication.class, args);

//		Region region = Region.US_EAST_1;
//		S3Client s3 = S3Client.builder()
//				.endpointOverride(URI.create("http://localhost:8000"))
//				.region(region)
//				.build();
//		ListBucketsResponse asd =  s3.listBuckets();
//		asd.buckets().forEach(x -> System.out.println(x.name()) );
	}

}

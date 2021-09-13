package com.matrust.zenko_cloudserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * configuration of access key and secret are on the app.prop
 *
 * never close the connection :
 *
 * Service clients in the SDK are thread-safe and, for best performance,
 * you should treat them as long-lived objects. Each client has its own connection pool resource.
 * Explicitly shut down clients when they are no longer needed to avoid resource leaks.
 *
 * https://stackoverflow.com/questions/26866739/how-do-i-close-an-aws-s3-client-connection
 *
 * since it's only 1 bean being shared across the whole app
 */
@Configuration
public class ZenkoS3ClientConfig {

    @Value("${zenko.s3.url}")
    private String url;

    @Value("${zenko.s3.region}")
    private String region;

    @Bean
    public S3Client zenkoS3Client(){
       return S3Client.builder()
                .endpointOverride(URI.create(url))
                .region(Region.of(region))
                .build();
    }


    // Create an S3Presigner using the default region and credentials.
    // This is usually done at application startup, because creating a presigner can be expensive.
    //https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/s3/presigner/S3Presigner.html
    @Bean
    public S3Presigner s3Presigner(){
        return S3Presigner.builder()
                .endpointOverride(URI.create(url))
                .region(Region.of(region))
                .build();
    }




}

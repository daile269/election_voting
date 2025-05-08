package com.datn.electronic_voting.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${s3.access-key}")
    private String ACCESS_KEY;

    @Value("${s3.secret-key}")
    private String SECRET_KEY;

    @Bean
    public S3Client s3Client(){
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(ACCESS_KEY,SECRET_KEY);

        return S3Client.builder()
                .region(Region.AP_SOUTHEAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}

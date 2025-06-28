package com.pagamento.aws.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.S3Presigner.Builder;

import java.net.URI;

@Configuration
public class S3ClientConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.endpoint:}")
    private String s3Endpoint;

    @Bean
    public S3Client s3Client(AwsCredentialsProvider credentialsProvider) {
        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider);

        if (s3Endpoint != null && !s3Endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(s3Endpoint));
        }

        return builder.build();
    }

    @Bean
    public S3Presigner s3Presigner(AwsCredentialsProvider credentialsProvider) {
        Builder builder = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider);

        if (s3Endpoint != null && !s3Endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(s3Endpoint));
        }

        return builder.build();
    }
}

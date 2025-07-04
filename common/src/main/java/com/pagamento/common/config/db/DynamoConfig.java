package com.pagamento.common.config.db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import java.net.URI;

@Configuration
public class DynamoConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.dynamodb.endpoint:}")
    private String dynamoEndpoint;

    @Bean
    public DynamoDbClient dynamoDbClient(AwsCredentialsProvider credentialsProvider) {
        // Use a construção correta do client
        DynamoDbClientBuilder builder = DynamoDbClient.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider);

        if (dynamoEndpoint != null && !dynamoEndpoint.isEmpty()) {
            builder.endpointOverride(URI.create(dynamoEndpoint));
        }

        return builder.build();
    }
}
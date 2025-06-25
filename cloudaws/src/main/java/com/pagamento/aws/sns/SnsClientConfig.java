package com.pagamento.aws.sns;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.SnsClientBuilder;

import java.net.URI;

@Configuration
public class SnsClientConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.sns.endpoint:}")
    private String snsEndpoint;

    @Bean
    public SnsClient snsClient(AwsCredentialsProvider credentialsProvider) {
        SnsClientBuilder builder = SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider);

        if (snsEndpoint != null && !snsEndpoint.isEmpty()) {
            builder.endpointOverride(URI.create(snsEndpoint));
        }

        return builder.build();
    }
}

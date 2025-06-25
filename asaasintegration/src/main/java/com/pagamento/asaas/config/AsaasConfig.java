package com.pagamento.asaas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsaasConfig {
    private final String apiKey;
    private final String baseUrl;

    public AsaasConfig(
        @Value("${asaas.api.key}") String apiKey,
        @Value("${asaas.api.base-url}") String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
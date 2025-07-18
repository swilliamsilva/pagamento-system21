package com.pagamento.pix.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;


@Configuration
public class BacenConfig {

    @Value("${bacen.api.url}")
    private String bacenApiUrl;

    @Value("${bacen.api.key}")
    private String bacenApiKey;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getBacenApiUrl() {
        return bacenApiUrl;
    }

    public String getBacenApiKey() {
        return bacenApiKey;
    }
    
    @PostConstruct
    public void validate() {
        if (!StringUtils.hasText(bacenApiUrl)) {
            throw new IllegalStateException("BACEN API URL não configurada");
        }
        
        if (!StringUtils.hasText(bacenApiKey)) {
            throw new IllegalStateException("BACEN API KEY não configurada");
        }
    }
}
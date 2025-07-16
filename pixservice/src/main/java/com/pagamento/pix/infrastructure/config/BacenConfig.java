package com.pagamento.pix.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bacen.api")
public class BacenConfig {
    private String url;
    private String key;

    // Getters e setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
}
package com.pagamento.pix.infrastructure.config;

import com.pagamento.pix.core.ports.out.BacenPort;
import com.pagamento.pix.infrastructure.adapters.BacenAdapter;
import com.pagamento.pix.infrastructure.integration.BacenClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class IntegrationConfig {

    @Bean
    public BacenClient bacenClient(RestTemplate restTemplate) {
        return new BacenClient(restTemplate);
    }

    @Bean
    public BacenPort bacenPort(BacenClient bacenClient) {
        return new BacenAdapter(bacenClient);
    }
}
package com.pagamento.pix.infrastructure.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration; // Corrigida a importação

@Configuration
public class ResilienceConfig {

    @Bean
    public static CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000)) // Usando java.time.Duration
                .slidingWindowSize(5)
                .minimumNumberOfCalls(10) // Exemplo adicional
                .permittedNumberOfCallsInHalfOpenState(3) // Exemplo adicional
                .build();
    }
}
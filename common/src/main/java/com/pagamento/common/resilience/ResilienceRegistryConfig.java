package com.pagamento.common.resilience;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import java.time.Duration;

@Configuration
public class ResilienceRegistryConfig {

    private static final String DEFAULT_CONFIG = "default";

    // Configuração simplificada sem RetryConfigCustomizer
    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig config = RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(500))
            .retryExceptions(Exception.class)
            .build();
            
        return RetryRegistry.of(config);
    }

    @Bean
    public Retry defaultRetry(RetryRegistry retryRegistry) {
        return retryRegistry.retry(DEFAULT_CONFIG);
    }
    
    // Configuração do Circuit Breaker
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(60)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .slidingWindowSize(20)
            .build();
            
        return CircuitBreakerRegistry.of(config);
    }
    
    @Bean
    public CircuitBreaker defaultCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker(DEFAULT_CONFIG);
    }
}
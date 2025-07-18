package com.pagamento.common.resilience;

import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;
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

    // Constante para o nome da configuração padrão
    private static final String DEFAULT_CONFIG = "default";

    // Bean para customização do RetryConfig
    @Bean
    public RetryConfigCustomizer retryConfigCustomizer() {
        // Configurações padrão para retry
        return (name, builder) -> 
            builder.maxAttempts(3)
                   .waitDuration(Duration.ofMillis(500))
                   .retryExceptions(Exception.class);
    }

    // Configuração do RetryRegistry
    @Bean
    public RetryRegistry retryRegistry(RetryConfigCustomizer retryConfigCustomizer) {
        RetryConfig baseConfig = RetryConfig.custom().build();
        RetryRegistry registry = RetryRegistry.of(baseConfig);
        
        // Aplica customização para a configuração padrão
        RetryConfig config = retryConfigCustomizer
            .customize(DEFAULT_CONFIG, RetryConfig.from(baseConfig))
            .build();
        
        registry.addConfiguration(DEFAULT_CONFIG, config);
        return registry;
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
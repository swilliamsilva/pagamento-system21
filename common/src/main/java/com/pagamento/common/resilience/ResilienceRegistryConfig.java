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

import java.time.Duration; // Import necessário para Duration

@Configuration
public class ResilienceRegistryConfig {

    // Configuração do Retry (versão única)
    @Bean
    public RetryRegistry retryRegistry(RetryConfigCustomizer retryConfigCustomizer) {
        RetryConfig.Builder builder = RetryConfig.custom();
        retryConfigCustomizer.customize("default", builder);
        return RetryRegistry.of(builder.build());
    }

    @Bean
    public Retry defaultRetry(RetryRegistry retryRegistry) {
        return retryRegistry.retry("default");
    }
    
    // Configuração do Circuit Breaker (corrigida)
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
        return registry.circuitBreaker("default");
    }
}
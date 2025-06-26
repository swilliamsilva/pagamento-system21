package com.pagamento.common.resilience;



import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;


@Configuration
public class ResilienceRegistryConfig {

    @Bean
    public RetryRegistry retryRegistry(RetryConfigCustomizer retryConfigCustomizer) {
        // 1. Criar o builder da configuração
        RetryConfig.Builder configBuilder = RetryConfig.custom();
        
        // 2. Aplicar customizações ao builder
        retryConfigCustomizer.customize("default", configBuilder);
        
        // 3. Construir a configuração final
        RetryConfig config = configBuilder.build();
        
        return RetryRegistry.of(config);
    }

    @Bean
    public Retry defaultRetry(RetryRegistry retryRegistry) {
        return retryRegistry.retry("default");
    }
    
    / Configuração do Circuit Breaker
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(60)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .slidingWindowSize(20)
            .build();
            
        return CircuitBreakerRegistry.of(config);
    }
    
}

    // Configuração do Retry (corrigida)
    @Bean
    public RetryRegistry retryRegistry(RetryConfigCustomizer retryConfigCustomizer) {
        RetryConfig.Builder builder = RetryConfig.custom();
        retryConfigCustomizer.customize("default", builder);
        return RetryRegistry.of(builder.build());
    }

    // Bean para uso direto
    @Bean
    public Retry defaultRetry(RetryRegistry retryRegistry) {
        return retryRegistry.retry("default");
    }
    
    // Bean para uso direto do Circuit Breaker
    @Bean
    public CircuitBreaker defaultCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("default");
    }
    
}
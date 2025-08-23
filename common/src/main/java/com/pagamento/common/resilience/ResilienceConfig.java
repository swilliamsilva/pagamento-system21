package com.pagamento.common.resilience;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@Configuration
public class ResilienceConfig {

    // Configuração principal do Circuit Breaker
    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .failureRateThreshold(60)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .permittedNumberOfCallsInHalfOpenState(5)
            .slidingWindowType(SlidingWindowType.COUNT_BASED)
            .slidingWindowSize(20)
            .minimumNumberOfCalls(10)
            .slowCallRateThreshold(80)
            .slowCallDurationThreshold(Duration.ofSeconds(3))
            .ignoreExceptions( // Exceções que não devem contar como falhas
                IllegalArgumentException.class, 
                IllegalStateException.class)
            .build();
    }

    // Configuração do TimeLimiter
    @Bean
    public TimeLimiterConfig timeLimiterConfig() {
        return TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(5))
            .cancelRunningFuture(true)
            .build();
    }

    // Configuração de Retry
    @Bean
    public RetryConfig retryConfig() {
        return RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(500))
            .retryExceptions(RuntimeException.class)
            .ignoreExceptions(IllegalArgumentException.class)
            .build();
    }

    // Registry para Circuit Breaker
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry(CircuitBreakerConfig config) {
        return CircuitBreakerRegistry.of(config);
    }

    // Registry para Retry
    @Bean
    public RetryRegistry retryRegistry(RetryConfig config) {
        return RetryRegistry.of(config);
    }

    // Customizador para integração com Spring Cloud Circuit Breaker
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> resilienceCustomizer(
            CircuitBreakerConfig circuitBreakerConfig,
            TimeLimiterConfig timeLimiterConfig) {
        
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .timeLimiterConfig(timeLimiterConfig)
            .circuitBreakerConfig(circuitBreakerConfig)
            .build());
    }

    // Bean de exemplo para injeção
    @Bean
    public CircuitBreaker paymentCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("paymentService", circuitBreakerConfig());
    }
}
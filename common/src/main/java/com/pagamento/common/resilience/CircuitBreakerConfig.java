package com.pagamento.common.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

/**
 * Configuração global para Circuit Breaker usando Resilience4j.
 */
@Configuration
public class CircuitBreakerConfigGlobal {

    @Bean
    public io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer defaultCircuitBreakerCustomizer() {
        return builder -> builder
            .failureRateThreshold(50) // Considera falha após 50% dos erros
            .waitDurationInOpenState(Duration.ofSeconds(10)) // Espera 10s antes

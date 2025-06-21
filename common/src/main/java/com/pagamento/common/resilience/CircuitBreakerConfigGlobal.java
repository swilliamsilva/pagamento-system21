package com.pagamento.common.resilience;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;

import java.time.Duration;

/**
 * Configuração global para Circuit Breaker usando Resilience4j.
 */
@Configuration
public class CircuitBreakerConfigGlobal {

    @Bean
    public CircuitBreakerConfigCustomizer defaultCustomizer() {
        return CircuitBreakerConfigCustomizer
            .of("default", builder -> builder
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(3)
                .slidingWindowSize(10)
            );
    }
}

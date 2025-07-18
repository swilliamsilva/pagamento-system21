package com.pagamento.pix.infrastructure.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    // Construtor privado para evitar instanciação
    private ResilienceConfig() {
        throw new UnsupportedOperationException("Esta é uma classe utilitária e não pode ser instanciada");
    }

    @Bean
    public static CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .slidingWindowSize(5)
                .minimumNumberOfCalls(10)
                .permittedNumberOfCallsInHalfOpenState(3)
                .build();
    }
}
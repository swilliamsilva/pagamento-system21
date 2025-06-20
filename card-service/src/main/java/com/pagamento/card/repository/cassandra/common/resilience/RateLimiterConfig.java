package com.pagamento.common.resilience;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuração global para Rate Limiter usando Resilience4j.
 */
@Configuration
public class RateLimiterConfigGlobal {

    @Bean
    public io.github.resilience4j.common.ratelimiter.configuration.RateLimiterConfigCustomizer defaultRateLimiterCustomizer() {
        return builder -> builder
            .limitForPeriod(10) // Permite até 10 chamadas
            .limitRefreshPeriod(Duration.ofSeconds(1)) // a cada 1 segundo
            .timeoutDuration(Duration.ofMillis(500)); // espera até 500ms por permissão
    }
}

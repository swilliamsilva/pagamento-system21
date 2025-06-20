package com.pagamento.common.resilience;

import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuração global para Retry usando Resilience4j.
 */
@Configuration
public class RetryConfigGlobal {

    @Bean
    public io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer defaultRetryCustomizer() {
        return builder -> builder
            .maxAttempts(3) // Tenta até 3 vezes
            .waitDuration(Duration.ofMillis(500)); // Aguarda 500ms entre tentativas
    }
}

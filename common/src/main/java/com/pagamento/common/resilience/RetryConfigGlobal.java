package com.pagamento.common.resilience;

import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class RetryConfigGlobal {

    @Bean
    public RetryConfigCustomizer defaultRetryCustomizer() {
        return RetryConfigCustomizer.of("default", builder -> builder
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(500))
            .retryOnException(e -> e instanceof BusinessException) 
            .ignoreExceptions(CircuitBreakerOpenException.class)
            .intervalFunction(IntervalFunction.ofExponentialRandomBackoff(
                Duration.ofMillis(500), 
                2.0,  // Multiplicador (não 1.5)
                0.5   // Fator de randomização (0.3-0.5 é ideal)
            ))
        );
    }
}
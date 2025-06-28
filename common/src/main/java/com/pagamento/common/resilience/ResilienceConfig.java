package com.pagamento.common.resilience;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
@Configuration
public class ResilienceConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> resilienceCustomizer() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(60) // % de falhas para abrir o circuito
                .waitDurationInOpenState(Duration.ofSeconds(30)) // Tempo em estado aberto
                .permittedNumberOfCallsInHalfOpenState(5) // Chamadas em meio-aberto
                .slidingWindowType(SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(20) // Tamanho da janela estatística
                .minimumNumberOfCalls(10) // Mínimo de chamadas para calcular estatísticas
                .slowCallRateThreshold(80) // % de chamadas lentas para abrir circuito
                .slowCallDurationThreshold(Duration.ofSeconds(3)) // Limite de lentidão
                .build();
/**
 * Valores Recomendados para Produção:
Configuração	Valor Típico	Descrição
slidingWindowSize	50-100	Número de chamadas na janela estatística
minimumNumberOfCalls	20	Mínimo antes de calcular métricas
waitDurationInOpenState	10-60s	Tempo no estado "Open"
failureRateThreshold	50-80%	% de falhas para abrir circuito
slowCallRateThreshold	50-100%	% de chamadas lentas para abrir circuito
slowCallDurationThreshold	1-5s	Limite para considerar chamada lenta
 
 */
        
        
        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(5)) // Timeout geral
                .build();

        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(timeLimiterConfig)
                .circuitBreakerConfig(circuitBreakerConfig)
                .build());
    }
}
package com.pagamento.gateway.filters;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

@Component
public class RateLimitingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-Key");

        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("Requisição sem X-API-Key. Bloqueando.");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        Bucket bucket = buckets.computeIfAbsent(apiKey, this::createNewBucket);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            exchange.getResponse().getHeaders().add("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return chain.filter(exchange);
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000;
            logger.warn("Limite de requisições excedido para API Key: {}. Tentar novamente em {}ms", apiKey, waitForRefill);
            exchange.getResponse().getHeaders().add("X-Rate-Limit-Retry-After-Milliseconds", String.valueOf(waitForRefill));
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }
    }

    private Bucket createNewBucket(String apiKey) {
        logger.info("Criando novo bucket para API Key: {}", apiKey);
        // Configuração do bucket: 10 requisições por minuto
        return Bucket4j.builder()
                .addLimit(limit -> limit.capacity(10).refillGreedy(10, Duration.ofMinutes(1)))
                .build();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}



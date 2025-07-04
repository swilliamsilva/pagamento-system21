package com.pagamento.gateway.filters;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(
    value = "filters.local-rate-limit.enabled", 
    havingValue = "true", 
    matchIfMissing = false
)
public class RateLimitingFilter implements GlobalFilter, Ordered {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    @Value("${filters.rate-limiting.capacity:5}")
    private int capacity;
    
    @Value("${filters.rate-limiting.refill-tokens:5}")
    private int refillTokens;
    
    @Value("${filters.rate-limiting.refill-period:60}")
    private int refillPeriodSeconds;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientKey = getClientIdentifier(exchange);
        Bucket bucket = buckets.computeIfAbsent(clientKey, key -> createNewBucket());
        
        if (bucket.tryConsume(1)) {
            addRateLimitHeaders(exchange, bucket);
            return chain.filter(exchange);
        } else {
            return handleRateLimitExceeded(exchange, bucket);
        }
    }

    private void addRateLimitHeaders(ServerWebExchange exchange, Bucket bucket) {
        long availableTokens = bucket.getAvailableTokens();
        exchange.getResponse().getHeaders().add(
            "X-Rate-Limit-Remaining", 
            String.valueOf(availableTokens)
        );
        exchange.getResponse().getHeaders().add(
            "X-Rate-Limit-Capacity", 
            String.valueOf(capacity)
        );
        
        long resetSeconds = 0;
        if (availableTokens <= 0) {
            resetSeconds = calculateTimeToNextToken(bucket);
        }
        
        exchange.getResponse().getHeaders().add(
            "X-Rate-Limit-Reset", 
            String.valueOf(resetSeconds)
        );
    }

    private long calculateTimeToNextToken(Bucket bucket) {
        // Usando abordagem alternativa para calcular o tempo de reset
        long consumedTokens = capacity - bucket.getAvailableTokens();
        double tokensPerSecond = (double) refillTokens / refillPeriodSeconds;
        return (long) Math.ceil(consumedTokens / tokensPerSecond);
    }

    private Mono<Void> handleRateLimitExceeded(ServerWebExchange exchange, Bucket bucket) {
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        long retryAfter = calculateTimeToNextToken(bucket);
        exchange.getResponse().getHeaders().add("Retry-After", String.valueOf(retryAfter));
        exchange.getResponse().getHeaders().add("X-Rate-Limit-Remaining", "0");
        return exchange.getResponse().setComplete();
    }

    private Bucket createNewBucket() {
        Refill refill = Refill.greedy(refillTokens, Duration.ofSeconds(refillPeriodSeconds));
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

    private String getClientIdentifier(ServerWebExchange exchange) {
        // 1. Tenta usar API Key se disponível
        String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-Key");
        if (apiKey != null && !apiKey.isBlank()) {
            return "api-key:" + apiKey;
        }
        
        // 2. Usa IP como fallback
        return "ip:" + getClientIp(exchange);
    }

    private String getClientIp(ServerWebExchange exchange) {
        // 1. Verifica X-Forwarded-For header
        String xff = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        
        // 2. Verificação segura para evitar NullPointerException
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        
        if (remoteAddress != null) {
            InetAddress address = remoteAddress.getAddress();
            if (address != null) {
                return address.getHostAddress();
            }
        }
        
        // 3. Fallback seguro
        return "unknown";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }

    public void clearBuckets() {
        buckets.clear();
    }

    public int getBucketCount() {
        return buckets.size();
    }
}
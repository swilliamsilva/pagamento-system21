package com.pagamento.gateway.filters;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
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
    
    private static final int CAPACITY = 5;
    private static final int REFILL_TOKENS = 5;
    private static final Duration REFILL_PERIOD = Duration.ofMinutes(1);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String ip = getClientIp(exchange);
        Bucket bucket = buckets.computeIfAbsent(ip, key -> createNewBucket());
        
        if (bucket.tryConsume(1)) {
            exchange.getResponse().getHeaders().add(
                "X-Rate-Limit-Remaining", 
                String.valueOf(bucket.getAvailableTokens())
            );
            exchange.getResponse().getHeaders().add(
                "X-Rate-Limit-Capacity", 
                String.valueOf(CAPACITY)
            );
            return chain.filter(exchange);
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().add("Retry-After", "60");
            exchange.getResponse().getHeaders().add("X-Rate-Limit-Remaining", "0");
            return exchange.getResponse().setComplete();
        }
    }

    private Bucket createNewBucket() {
        Refill refill = Refill.greedy(REFILL_TOKENS, REFILL_PERIOD);
        Bandwidth limit = Bandwidth.classic(CAPACITY, refill);
        return Bucket.builder()
            .addLimit(limit)
            .build();
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
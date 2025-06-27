package com.pagamento.gateway.filters;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucket;
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

@Component
public class RateLimitingFilter implements GlobalFilter, Ordered {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. Obter IP do cliente considerando proxies
        String ip = getClientIp(exchange);
        
        // 2. Obter ou criar bucket para o IP
        Bucket bucket = buckets.computeIfAbsent(ip, key -> createNewBucket());
        
        // 3. Verificar limite de taxa
        if (bucket.tryConsume(1)) {
            return chain.filter(exchange);
        } else {
            // 4. Limite excedido - retornar erro
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().add("Retry-After", "60");
            exchange.getResponse().getHeaders().add("X-Rate-Limit-Remaining", "0");
            return exchange.getResponse().setComplete();
        }
    }

    private Bucket createNewBucket() {
        // 100 requisições por minuto por IP
        Bandwidth limit = Bandwidth.classic(100, 
            Refill.greedy(100, Duration.ofMinutes(1)));
        
        return Bucket4j.builder()
            .addLimit(limit)
            .build();
    }

    private String getClientIp(ServerWebExchange exchange) {
        // Considerar cabeçalhos X-Forwarded-For em ambientes com proxy
        String xff = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return exchange.getRequest().getRemoteAddress() != null ? 
               exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : 
               "unknown";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }

	public void clearBuckets() {
		// TODO Auto-generated method stub
		
	}
}
package com.pagamento.gateway;

import com.pagamento.gateway.filters.RateLimitingFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitingFilterTest {

    @InjectMocks
    private RateLimitingFilter rateLimitingFilter;

    @Mock
    private GatewayFilterChain filterChain;

    @AfterEach
    void resetRateLimiter() {
        rateLimitingFilter.clearBuckets();
    }

    private MockServerWebExchange createExchange() {
        try {
            InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
            InetSocketAddress socketAddress = new InetSocketAddress(inetAddress, 8080);
            
            return MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/resource")
                    .remoteAddress(socketAddress)
                    .header("X-Forwarded-For", "192.168.1.100, 10.0.0.1")
                    .build()
            );
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldAllowRequestWithinLimit() {
        // Arrange
        ServerWebExchange exchange = createExchange();
        
        // Configura comportamento do filterChain
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        // Act
        Mono<Void> result = rateLimitingFilter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
            
        // Verifica que o status não foi alterado (request permitida)
        assertNull(exchange.getResponse().getStatusCode(), 
            "Status não deve ser definido para requests dentro do limite");
            
        // Verifica headers de rate limit
        assertEquals("4", exchange.getResponse().getHeaders().getFirst("X-Rate-Limit-Remaining"),
            "Deveria ter 4 tokens restantes após 1 consumo");
        assertEquals("5", exchange.getResponse().getHeaders().getFirst("X-Rate-Limit-Capacity"),
            "Capacidade deve ser 5");
    }

    @Test
    void shouldBlockRequestExceedingLimit() {
        // Configura comportamento do filterChain
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        // Primeiras 5 requisições (limite) devem passar
        for (int i = 0; i < 5; i++) {
            ServerWebExchange exchange = createExchange();
            StepVerifier.create(rateLimitingFilter.filter(exchange, filterChain))
                .verifyComplete();
        }
        
        // 6ª requisição deve ser bloqueada
        ServerWebExchange blockedExchange = createExchange();
        
        // Act
        Mono<Void> result = rateLimitingFilter.filter(blockedExchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
            
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, blockedExchange.getResponse().getStatusCode(),
            "Deveria retornar 429 Too Many Requests");
        assertEquals("60", blockedExchange.getResponse().getHeaders().getFirst("Retry-After"),
            "Deveria ter header Retry-After=60");
        assertEquals("0", blockedExchange.getResponse().getHeaders().getFirst("X-Rate-Limit-Remaining"),
            "Deveria ter 0 tokens restantes");
    }
    
    @Test
    void shouldUseXForwardedForHeader() {
        // Arrange
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/resource")
                .header("X-Forwarded-For", "203.0.113.195, 70.41.3.18, 150.172.238.178")
                .build()
        );
        
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        // Act
        Mono<Void> result = rateLimitingFilter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result).verifyComplete();
        
        // Verifica se o IP usado foi o primeiro do X-Forwarded-For
        assertEquals(1, rateLimitingFilter.getBucketCount(),
            "Deveria ter criado bucket para o IP 203.0.113.195");
    }
}
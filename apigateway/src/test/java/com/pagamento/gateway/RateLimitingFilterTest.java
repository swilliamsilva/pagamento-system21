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
        assertEquals(null, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldBlockRequestExceedingLimit() {
        // Configura comportamento do filterChain
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        // Primeiras 100 requisições (limite) devem passar
        for (int i = 0; i < 100; i++) {
            ServerWebExchange exchange = createExchange();
            rateLimitingFilter.filter(exchange, filterChain).block();
        }
        
        // 101ª requisição deve ser bloqueada
        ServerWebExchange blockedExchange = createExchange();
        
        // Act
        Mono<Void> result = rateLimitingFilter.filter(blockedExchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
            
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, blockedExchange.getResponse().getStatusCode());
        assertEquals("60", blockedExchange.getResponse().getHeaders().getFirst("Retry-After"));
    }
}
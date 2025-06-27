package com.pagamento.gateway;

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

import com.pagamento.gateway.filters.SecurityFilter;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @InjectMocks
    private SecurityFilter securityFilter;

    @Mock
    private GatewayFilterChain filterChain;

    private MockServerWebExchange createExchange(String path) {
        return MockServerWebExchange.from(MockServerHttpRequest.get(path).build());
    }

    @Test
    void shouldBlockPathWithSemicolon() {
        // Arrange
        ServerWebExchange exchange = createExchange("/test;param=attack");
        when(filterChain.filter(any())).thenReturn(Mono.empty());
        
        // Act
        Mono<Void> result = securityFilter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldBlockRestrictedPathWithoutAdminKey() {
        // Arrange
        ServerWebExchange exchange = createExchange("/actuator/health");
        when(filterChain.filter(any())).thenReturn(Mono.empty());
        
        // Act
        Mono<Void> result = securityFilter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldAllowRestrictedPathWithAdminKey() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/actuator/health")
            .header("X-Admin-Key", "ADMIN_SECRET_123")
            .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        when(filterChain.filter(any())).thenReturn(Mono.empty());
        
        // Act
        Mono<Void> result = securityFilter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
        assertEquals(HttpStatus.OK, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldBlockSqlInjectionKeywords() {
        // Arrange
        ServerWebExchange exchange = createExchange("/users?query=DELETE");
        when(filterChain.filter(any())).thenReturn(Mono.empty());
        
        // Act
        Mono<Void> result = securityFilter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldAllowValidPath() {
        // Arrange
        ServerWebExchange exchange = createExchange("/api/resource");
        when(filterChain.filter(any())).thenReturn(Mono.empty());
        
        // Act
        Mono<Void> result = securityFilter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
        assertEquals(HttpStatus.OK, exchange.getResponse().getStatusCode());
    }
}
package com.pagamento.gateway.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Supplier;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class CorrelationIdFilterTest {

    private CorrelationIdFilter filter;

    @BeforeEach
    void setUp() {
        filter = new CorrelationIdFilter();
    }

    @Test
    void shouldAddCorrelationIdWhenMissing() {
        ServerHttpRequest mockRequest = mock(ServerHttpRequest.class);
        ServerHttpRequest.Builder mockRequestBuilder = mock(ServerHttpRequest.Builder.class);
        ServerWebExchange mockExchange = mock(ServerWebExchange.class);
        ServerWebExchange.Builder mockExchangeBuilder = mock(ServerWebExchange.Builder.class);
        ServerWebExchange mutatedExchange = mock(ServerWebExchange.class);
        GatewayFilterChain mockChain = mock(GatewayFilterChain.class);
        ServerHttpResponse mockResponse = mock(ServerHttpResponse.class);

        HttpHeaders headers = new HttpHeaders(); // Sem Correlation ID
        when(mockRequest.getHeaders()).thenReturn(headers);
        when(mockRequest.mutate()).thenReturn(mockRequestBuilder);
        when(mockRequestBuilder.header(eq(CorrelationIdFilter.CORRELATION_ID_HEADER), anyString()))
                .thenReturn(mockRequestBuilder);
        when(mockRequestBuilder.build()).thenReturn(mockRequest);

        when(mockExchange.getRequest()).thenReturn(mockRequest);
        when(mockExchange.getResponse()).thenReturn(mockResponse);
        when(mockExchange.mutate()).thenReturn(mockExchangeBuilder);
        when(mockExchangeBuilder.request(any(ServerHttpRequest.class))).thenReturn(mockExchangeBuilder);
        when(mockExchangeBuilder.build()).thenReturn(mutatedExchange);

        when(mutatedExchange.getResponse()).thenReturn(mockResponse);
        when(mockResponse.getHeaders()).thenReturn(new HttpHeaders());

        // CORREÇÃO: Usar doAnswer() para beforeCommit que retorna void, executando o Supplier
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Supplier<Mono<Void>> supplier = (Supplier<Mono<Void>>) invocation.getArgument(0);
            return supplier.get();
        }).when(mockResponse).beforeCommit(any());

        when(mockChain.filter(mutatedExchange)).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(mockExchange, mockChain);

        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void shouldUseExistingCorrelationId() {
        String existingId = "test-id-123";

        ServerHttpRequest mockRequest = mock(ServerHttpRequest.class);
        ServerHttpRequest.Builder mockRequestBuilder = mock(ServerHttpRequest.Builder.class);
        ServerWebExchange mockExchange = mock(ServerWebExchange.class);
        ServerWebExchange.Builder mockExchangeBuilder = mock(ServerWebExchange.Builder.class);
        ServerWebExchange mutatedExchange = mock(ServerWebExchange.class);
        GatewayFilterChain mockChain = mock(GatewayFilterChain.class);
        ServerHttpResponse mockResponse = mock(ServerHttpResponse.class);

        HttpHeaders headers = new HttpHeaders();
        headers.add(CorrelationIdFilter.CORRELATION_ID_HEADER, existingId);
        when(mockRequest.getHeaders()).thenReturn(headers);
        when(mockRequest.mutate()).thenReturn(mockRequestBuilder);
        when(mockRequestBuilder.header(eq(CorrelationIdFilter.CORRELATION_ID_HEADER), eq(existingId)))
                .thenReturn(mockRequestBuilder);
        when(mockRequestBuilder.build()).thenReturn(mockRequest);

        when(mockExchange.getRequest()).thenReturn(mockRequest);
        when(mockExchange.getResponse()).thenReturn(mockResponse);
        when(mockExchange.mutate()).thenReturn(mockExchangeBuilder);
        when(mockExchangeBuilder.request(any(ServerHttpRequest.class))).thenReturn(mockExchangeBuilder);
        when(mockExchangeBuilder.build()).thenReturn(mutatedExchange);

        when(mutatedExchange.getResponse()).thenReturn(mockResponse);
        when(mockResponse.getHeaders()).thenReturn(new HttpHeaders());

        // Mesma correção do método acima
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Supplier<Mono<Void>> supplier = (Supplier<Mono<Void>>) invocation.getArgument(0);
            return supplier.get();
        }).when(mockResponse).beforeCommit(any());

        when(mockChain.filter(mutatedExchange)).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(mockExchange, mockChain);

        StepVerifier.create(result).verifyComplete();
    }
}

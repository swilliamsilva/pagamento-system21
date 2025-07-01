package com.pagamento.gateway.filters;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchange.Builder;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CorrelationIdFilterTest {

    private CorrelationIdFilter filter;

    private ServerWebExchange exchange;
    private Builder exchangeBuilder;
    private GatewayFilterChain chain;

    private ServerHttpRequest request;
    private ServerHttpRequest.Builder requestBuilder;

    private ServerHttpResponse response;
    private HttpHeaders responseHeaders;

    @BeforeEach
    void setup() {
        filter = new CorrelationIdFilter();

        request = mock(ServerHttpRequest.class);
        requestBuilder = mock(ServerHttpRequest.Builder.class);

        response = mock(ServerHttpResponse.class);
        responseHeaders = new HttpHeaders();

        exchange = mock(ServerWebExchange.class);
        exchangeBuilder = mock(Builder.class);
        chain = mock(GatewayFilterChain.class);

        // mocks para exchange e builder
        when(exchange.getRequest()).thenReturn(request);
        when(exchange.mutate()).thenReturn(exchangeBuilder);
        when(exchangeBuilder.request(any(ServerHttpRequest.class))).thenReturn(exchangeBuilder);
        when(exchangeBuilder.build()).thenReturn(exchange);

        // mocks para request e builder
        when(request.mutate()).thenReturn(requestBuilder);
        when(requestBuilder.header(anyString(), anyString())).thenReturn(requestBuilder);
        when(requestBuilder.build()).thenReturn(request);

        // mocks para response e headers
        when(exchange.getResponse()).thenReturn(response);
        when(response.getHeaders()).thenReturn(responseHeaders);

        // mock para chain
        when(chain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    void shouldAddCorrelationIdWhenMissing() {
        when(request.getHeaders()).thenReturn(HttpHeaders.EMPTY);

        Mono<Void> result = filter.filter(exchange, chain);

        StepVerifier.create(result)
            .verifyComplete();

        // Verifica se adicionou no request o header X-Correlation-Id
        verify(requestBuilder).header(eq(CorrelationIdFilter.CORRELATION_ID_HEADER), anyString());

        // Verifica se adicionou no response o header X-Correlation-Id
        assertTrue(responseHeaders.containsKey(CorrelationIdFilter.CORRELATION_ID_HEADER));
        List<String> values = responseHeaders.get(CorrelationIdFilter.CORRELATION_ID_HEADER);
        assertNotNull(values);
        assertFalse(values.isEmpty());
    }

    @Test
    void shouldUseExistingCorrelationId() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CorrelationIdFilter.CORRELATION_ID_HEADER, "existing-id");
        when(request.getHeaders()).thenReturn(headers);

        Mono<Void> result = filter.filter(exchange, chain);

        StepVerifier.create(result)
            .verifyComplete();

        verify(requestBuilder).header(eq(CorrelationIdFilter.CORRELATION_ID_HEADER), eq("existing-id"));
        assertTrue(responseHeaders.containsKey(CorrelationIdFilter.CORRELATION_ID_HEADER));
        assertEquals("existing-id", responseHeaders.getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER));
    }
}

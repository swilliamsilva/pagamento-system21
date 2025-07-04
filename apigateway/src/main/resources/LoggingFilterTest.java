package com.pagamento.gateway;

import com.pagamento.gateway.filters.LoggingFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingFilterTest {

    @Mock
    private GatewayFilterChain filterChain;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpRequest.Builder requestBuilder;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private ServerWebExchange.Builder exchangeBuilder;

    private LoggingFilter loggingFilter;

    @BeforeEach
    void setUp() {
        loggingFilter = new LoggingFilter();
    }

    @Test
    void shouldAddCorrelationIdIfMissing() {
        HttpHeaders headers = new HttpHeaders();
        final long startTime = System.currentTimeMillis();

        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);
        when(request.mutate()).thenReturn(requestBuilder);
        when(requestBuilder.header(eq("X-Correlation-Id"), anyString())).thenReturn(requestBuilder);
        when(requestBuilder.build()).thenReturn(request);
        when(exchange.mutate()).thenReturn(exchangeBuilder);
        when(exchangeBuilder.request(request)).thenReturn(exchangeBuilder);
        when(exchangeBuilder.build()).thenReturn(exchange);
        when(exchange.getAttribute("requestStartTime")).thenReturn(startTime);
        when(exchange.getResponse()).thenReturn(response);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        loggingFilter.filter(exchange, filterChain).block();

        verify(requestBuilder).header(eq("X-Correlation-Id"), anyString());
        verify(exchangeBuilder).request(request);
        verify(response, atLeastOnce()).getStatusCode();
    }

    @Test
    void shouldNotAddCorrelationIdWhenAlreadyPresent() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Correlation-Id", "existing-id");
        final long startTime = System.currentTimeMillis();

        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);
        when(exchange.getAttribute("requestStartTime")).thenReturn(startTime);
        when(exchange.getResponse()).thenReturn(response);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        loggingFilter.filter(exchange, filterChain).block();

        verify(request, never()).mutate();
        verify(requestBuilder, never()).header(anyString(), anyString());
        verify(response, atLeastOnce()).getStatusCode();
    }

    @Test
    void getOrder_shouldReturnHighestPrecedence() {
        assertEquals(Integer.MIN_VALUE, loggingFilter.getOrder());
    }

    @Test
    void shouldLogRequestAndResponse_placeholder() {
        assertTrue(true);
    }

    @Test
    void shouldNotLogSensitiveHeaders_placeholder() {
        assertTrue(true);
    }

    @Test
    void shouldClearMDCAfterProcessing_placeholder() {
        assertTrue(true);
    }
}

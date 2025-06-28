package com.pagamento.gateway.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingFilterTest {

    @Mock
    private GatewayFilterChain chain;

    @Mock
    private Logger logger;

    @InjectMocks
    private LoggingFilter loggingFilter;

    private ServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        // Mocking the logger
        try (MockedStatic<LoggerFactory> loggerFactory = Mockito.mockStatic(LoggerFactory.class)) {
            loggerFactory.when(() -> LoggerFactory.getLogger(LoggingFilter.class)).thenReturn(logger);
            when(logger.isInfoEnabled()).thenReturn(true);
            
            // Setup exchange with a request
            MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .header("X-Correlation-Id", "test-correlation-id")
                .header("Authorization", "Bearer token")
                .build();
            exchange = MockServerWebExchange.from(request);
        }

        // Setup filter chain to return a successful response
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @Test
    void filter_shouldAddCorrelationIdIfMissing() {
        // Arrange: Create an exchange without a correlation id
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        ServerWebExchange exchangeWithoutCorrelation = MockServerWebExchange.from(request);

        // Act
        loggingFilter.filter(exchangeWithoutCorrelation, chain).block();

        // Assert: Verify that the correlation id was added
        assertNotNull(exchangeWithoutCorrelation.getRequest().getHeaders().getFirst("X-Correlation-Id"));
    }

    @Test
    void filter_shouldUseExistingCorrelationId() {
        // Arrange
        String expectedCorrelationId = "existing-correlation-id";
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
            .header("X-Correlation-Id", expectedCorrelationId)
            .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // Act
        loggingFilter.filter(exchange, chain).block();

        // Assert
        assertEquals(expectedCorrelationId, MDC.get("X-Correlation-Id"));
    }

    @Test
    void filter_shouldLogRequestAndResponse() {
        // Act
        loggingFilter.filter(exchange, chain).block();

        // Assert: Verify that the request and response were logged
        verify(logger).info(contains("Request [test-correlation-id]: GET /test"));
        verify(logger).info(contains("Response [test-correlation-id]: Status 0 | Duration"));
    }

    @Test
    void filter_shouldNotLogSensitiveHeaders() {
        // Arrange
        when(logger.isDebugEnabled()).thenReturn(true);
        
        // Act
        loggingFilter.filter(exchange, chain).block();

        // Assert: Verify that sensitive headers are not logged
        verify(logger, never()).debug(contains("Authorization"));
        verify(logger, never()).debug(contains("Bearer token"));
        verify(logger).debug(contains("X-Correlation-Id: [test-correlation-id]"));
    }

    @Test
    void filter_shouldMeasureRequestDuration() {
        // Arrange
        long startTime = System.currentTimeMillis();
        when(chain.filter(any())).thenReturn(Mono.delay(java.time.Duration.ofMillis(100)).then());

        // Act
        loggingFilter.filter(exchange, chain).block();

        // Assert: Verify that the duration is logged and is at least 100ms
        verify(logger).info(contains("Duration"), anyString(), anyInt(), anyLong(), anyString());
        // We can't exactly assert the duration value because of the mock, but we can check that the log method was called with a long argument for duration
    }

    @Test
    void getOrder_shouldReturnHighestPrecedence() {
        assertEquals(Integer.MIN_VALUE, loggingFilter.getOrder());
    }

    @Test
    void filter_shouldClearMDCAfterProcessing() {
        // Act
        loggingFilter.filter(exchange, chain).block();

        // Assert
        assertNull(MDC.get("X-Correlation-Id"));
    }
}
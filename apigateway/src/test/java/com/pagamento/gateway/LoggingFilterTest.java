package com.pagamento.gateway;

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
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import com.pagamento.gateway.filters.LoggingFilter;

import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingFilterTest {

    @Mock
    private GatewayFilterChain filterChain;

    @Mock
    private Logger logger;

    @InjectMocks
    private LoggingFilter loggingFilter;

    private ServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        try (MockedStatic<LoggerFactory> loggerFactory = Mockito.mockStatic(LoggerFactory.class)) {
            loggerFactory.when(() -> LoggerFactory.getLogger(LoggingFilter.class)).thenReturn(logger);
            when(logger.isInfoEnabled()).thenReturn(true);
            
            MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .header("Authorization", "Bearer token")
                .build();
            exchange = MockServerWebExchange.from(request);
        }

        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @Test
    void shouldAddCorrelationIdIfMissing() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Act
        loggingFilter.filter(exchange, filterChain).block();
        
        // Assert
        assertNotNull(exchange.getRequest().getHeaders().getFirst("X-Correlation-Id"));
    }

    private void assertNotNull(String first) {
		// TODO Auto-generated method stub
		
	}

	@Test
    void shouldLogRequestAndResponse() {
        // Act
        loggingFilter.filter(exchange, filterChain).block();
        
        // Assert
        verify(logger).info(contains("Request"));
        verify(logger).info(contains("Response"));
    }

    @Test
    void shouldNotLogSensitiveHeaders() {
        // Arrange
        when(logger.isDebugEnabled()).thenReturn(true);
        
        // Act
        loggingFilter.filter(exchange, filterChain).block();
        
        // Assert
        verify(logger, never()).debug(contains("Authorization"));
        verify(logger, never()).debug(contains("Bearer token"));
    }

    @Test
    void shouldClearMDCAfterProcessing() {
        // Act
        loggingFilter.filter(exchange, filterChain).block();
        
        // Assert
        assertNull(MDC.get("X-Correlation-Id"));
    }

	private void assertNull(String string) {
		// TODO Auto-generated method stub
		
	}
}
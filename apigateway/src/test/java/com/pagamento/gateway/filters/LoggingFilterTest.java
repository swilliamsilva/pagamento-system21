package com.pagamento.gateway.filters;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class LoggingFilterTest {

    @Mock
    private GatewayFilterChain chain;

    @Mock
    private Logger logger;

    private LoggingFilter loggingFilter;
    private ServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        // Usando o construtor que aceita logger mockado
        loggingFilter = new LoggingFilter(logger);
        
        when(logger.isInfoEnabled()).thenReturn(true);
        when(logger.isDebugEnabled()).thenReturn(false);

        MockServerHttpRequest request = MockServerHttpRequest
            .get("/test")
            .header("X-Correlation-Id", "test-correlation-id")
            .header("Authorization", "Bearer token")
            .build();
        exchange = MockServerWebExchange.from(request);

        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @AfterEach
    void tearDown() {
        MDC.clear(); // Limpa MDC após cada teste
    }

    @Test
    void filter_shouldAddCorrelationIdIfMissing() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        ServerWebExchange exchangeWithoutCorrelation = MockServerWebExchange.from(request);

        loggingFilter.filter(exchangeWithoutCorrelation, chain).block();

        assertNotNull(exchangeWithoutCorrelation.getRequest().getHeaders().getFirst("X-Correlation-Id"));
    }

    @Test
    void filter_shouldUseExistingCorrelationId() {
        String expectedCorrelationId = "existing-correlation-id";
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
            .header("X-Correlation-Id", expectedCorrelationId)
            .build();
        ServerWebExchange exchangeWithCorrelation = MockServerWebExchange.from(request);

        loggingFilter.filter(exchangeWithCorrelation, chain).block();

        assertEquals(expectedCorrelationId,
            exchangeWithCorrelation.getRequest().getHeaders().getFirst("X-Correlation-Id"));
    }

    @Test
    void filter_shouldLogRequestAndResponse() {
        loggingFilter.filter(exchange, chain).block();

        verify(logger).info(argThat(msg -> msg.contains("Request [test-correlation-id]: GET /test")));
        verify(logger).info(argThat(msg -> msg.startsWith("Response [test-correlation-id]: Status")));
    }

    @Test
    void filter_shouldNotLogSensitiveHeaders() {
        when(logger.isDebugEnabled()).thenReturn(true);

        loggingFilter.filter(exchange, chain).block();

        // Verifica se headers sensíveis não são logados
        verify(logger, never()).debug(argThat(msg -> 
            msg.contains("Authorization") || 
            msg.contains("Bearer token")
        ));
        
        // Verifica se headers não sensíveis são logados
        verify(logger).debug(argThat(msg -> 
            msg.contains("X-Correlation-Id: [test-correlation-id]")
        ));
    }

    @Test
    void filter_shouldMeasureRequestDuration() {
        when(chain.filter(any())).thenReturn(Mono.delay(java.time.Duration.ofMillis(100)).then());

        loggingFilter.filter(exchange, chain).block();

        verify(logger).info(argThat(msg -> 
            msg.startsWith("Response [test-correlation-id]: Status") && 
            msg.contains("ms")
        ));
    }

    @Test
    void getOrder_shouldReturnHighestPrecedence() {
        assertEquals(Integer.MIN_VALUE, loggingFilter.getOrder());
    }

    @Test
    void filter_shouldClearMDCAfterProcessing() {
        MDC.put("X-Correlation-Id", "test-value");

        loggingFilter.filter(exchange, chain).block();

        assertNull(MDC.get("X-Correlation-Id"));
    }
}

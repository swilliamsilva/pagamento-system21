package com.pagamento.gateway.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import ch.qos.logback.classic.Logger;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class LoggingFilterTest {

    @Mock
    private GatewayFilterChain cadeiaFiltros;

    @Mock
    private Logger logger;

    private LoggingFilter filtroLogging;
    private ServerWebExchange exchange;

    @BeforeEach
    void configurar() {
        filtroLogging = new LoggingFilter(logger);

        when(logger.isInfoEnabled()).thenReturn(true);
        when(logger.isDebugEnabled()).thenReturn(true);

        MockServerHttpRequest request = MockServerHttpRequest
            .get("/teste")
            .header("X-Correlation-Id", "teste-correlation-id")
            .header("Authorization", "Bearer token")
            .header("X-Sensitive", "dado-sensivel")
            .build();
        exchange = MockServerWebExchange.from(request);

        when(cadeiaFiltros.filter(any(ServerWebExchange.class)))
            .thenAnswer(invocacao -> {
                ServerWebExchange ex = invocacao.getArgument(0);
                ex.getResponse().setStatusCode(HttpStatus.OK);
                return Mono.empty();
            });
    }

    @Test
    void deveAdicionarCorrelationIdQuandoAusente() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/teste").build();
        ServerWebExchange exchangeSemCorrelation = MockServerWebExchange.from(request);

        filtroLogging.filter(exchangeSemCorrelation, cadeiaFiltros).block();

        assertNotNull(exchangeSemCorrelation.getRequest().getHeaders().getFirst("X-Correlation-Id"));
    }

    @Test
    void deveUtilizarCorrelationIdExistente() {
        String correlationIdEsperado = "correlation-id-existente";
        MockServerHttpRequest request = MockServerHttpRequest.get("/teste")
            .header("X-Correlation-Id", correlationIdEsperado)
            .build();
        ServerWebExchange exchangeComCorrelation = MockServerWebExchange.from(request);

        filtroLogging.filter(exchangeComCorrelation, cadeiaFiltros).block();

        assertEquals(correlationIdEsperado,
            exchangeComCorrelation.getRequest().getHeaders().getFirst("X-Correlation-Id"));
    }

    @Test
    void deveRegistrarLogDeRequisicao() {
        filtroLogging.filter(exchange, cadeiaFiltros).block();

        verify(logger).info(argThat(msg ->
            msg.contains("Request [teste-correlation-id]: GET /teste")
        ));
    }

    @Test
    void deveRegistrarLogDeResposta() {
        filtroLogging.filter(exchange, cadeiaFiltros).block();

        verify(logger).info(eq("Response [{}]: Status {} | Duration {}ms | Path: {}"),
            eq("teste-correlation-id"), eq(200), anyLong(), eq("/teste"));
    }

    @Test
    void naoDeveLogarDadosSensiveis() {
        when(logger.isDebugEnabled()).thenReturn(true);
        filtroLogging.filter(exchange, cadeiaFiltros).block();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(logger, atLeastOnce()).info(captor.capture());

        List<String> logs = captor.getAllValues();
        for (String msg : logs) {
            // Verificamos os valores sensíveis, não os nomes dos headers
            assertFalse(msg.contains("Bearer token"), "Mensagem contém dado sensível: " + msg);
            assertFalse(msg.contains("dado-sensivel"), "Mensagem contém dado sensível: " + msg);
        }
    }

    @Test
    void deveMedirTempoDeProcessamento() {
        when(cadeiaFiltros.filter(any(ServerWebExchange.class)))
            .thenAnswer(invocacao -> {
                ServerWebExchange ex = invocacao.getArgument(0);
                ex.getResponse().setStatusCode(HttpStatus.OK);
                return Mono.delay(java.time.Duration.ofMillis(50)).then();
            });

        filtroLogging.filter(exchange, cadeiaFiltros).block();

        verify(logger).info(eq("Response [{}]: Status {} | Duration {}ms | Path: {}"),
            eq("teste-correlation-id"), eq(200), anyLong(), eq("/teste"));
    }

    @Test
    void deveRetornarOrdemCorreta() {
        assertEquals(Ordered.HIGHEST_PRECEDENCE + 1000, filtroLogging.getOrder());
    }

    @Test
    void devePropagarCorrelationIdNoContexto() {
        when(cadeiaFiltros.filter(any())).thenAnswer(invocacao -> {
            // Renomeado para evitar conflito com o campo da classe
            ServerWebExchange ex = invocacao.getArgument(0);
            String correlationId = ex.getRequest().getHeaders().getFirst("X-Correlation-Id");

            return Mono.deferContextual(contextView -> {
                String ctxCorrelationId = contextView.getOrDefault("X-Correlation-Id", "");
                assertEquals(correlationId, ctxCorrelationId);
                return Mono.empty();
            });
        });

        filtroLogging.filter(exchange, cadeiaFiltros).block();
    }

    @Test
    void deveRegistrarLogsApenasQuandoInfoHabilitado() {
        when(logger.isInfoEnabled()).thenReturn(false);

        filtroLogging.filter(exchange, cadeiaFiltros).block();

        verify(logger, never()).info(anyString());
    }

    @Test
    void deveRegistrarLogDeRespostaMesmoComErro() {
        when(cadeiaFiltros.filter(any())).thenAnswer(invocacao -> {
            ServerWebExchange ex = invocacao.getArgument(0);
            ex.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return Mono.error(new RuntimeException("Erro simulado"));
        });

        filtroLogging.filter(exchange, cadeiaFiltros).onErrorResume(e -> Mono.empty()).block();

        verify(logger).info(eq("Response [{}]: Status {} | Duration {}ms | Path: {}"),
            eq("teste-correlation-id"), eq(500), anyLong(), eq("/teste"));
    }
}
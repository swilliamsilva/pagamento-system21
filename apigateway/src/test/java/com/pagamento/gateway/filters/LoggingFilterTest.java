package com.pagamento.gateway.filters;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import ch.qos.logback.classic.Logger;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * Testes para o filtro de logging do Gateway.
 * 
 * <p>Verifica o comportamento do filtro em relação a:
 * <ul>
 *   <li>Geração e propagação de Correlation ID</li>
 *   <li>Registro de logs de requisições e respostas</li>
 *   <li>Proteção de dados sensíveis nos logs</li>
 *   <li>Medição de tempo de processamento</li>
 *   <li>Propagação de contexto</li>
 * </ul>
 */
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

        when(cadeiaFiltros.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    /**
     * Verifica se o filtro adiciona um Correlation ID quando ausente.
     */
    @Test
    void deveAdicionarCorrelationIdQuandoAusente() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/teste").build();
        ServerWebExchange exchangeSemCorrelation = MockServerWebExchange.from(request);

        filtroLogging.filter(exchangeSemCorrelation, cadeiaFiltros).block();

        assertNotNull(exchangeSemCorrelation.getRequest().getHeaders().getFirst("X-Correlation-Id"),
            "Deve gerar um Correlation ID quando ausente");
    }

    /**
     * Verifica se o filtro utiliza um Correlation ID existente.
     */
    @Test
    void deveUtilizarCorrelationIdExistente() {
        String correlationIdEsperado = "correlation-id-existente";
        MockServerHttpRequest request = MockServerHttpRequest.get("/teste")
            .header("X-Correlation-Id", correlationIdEsperado)
            .build();
        ServerWebExchange exchangeComCorrelation = MockServerWebExchange.from(request);

        filtroLogging.filter(exchangeComCorrelation, cadeiaFiltros).block();

        assertEquals(correlationIdEsperado,
            exchangeComCorrelation.getRequest().getHeaders().getFirst("X-Correlation-Id"),
            "Deve manter o Correlation ID existente");
    }

    /**
     * Verifica se o filtro registra logs de requisição e resposta.
     */
    @Test
    void deveRegistrarLogsDeRequisicaoEResposta() {
        filtroLogging.filter(exchange, cadeiaFiltros).block();

        verify(logger).info(argThat(msg -> 
            msg.contains("Request [teste-correlation-id]: GET /teste")
        ));
        verify(logger).info(argThat(msg -> 
            msg.startsWith("Response [teste-correlation-id]: Status") &&
            msg.contains("Path: /teste")
        ));
    }

    /**
     * Verifica se o filtro protege dados sensíveis nos logs.
     */
    @Test
    void naoDeveLogarDadosSensiveis() {
        filtroLogging.filter(exchange, cadeiaFiltros).block();

        // Verifica que headers sensíveis não aparecem no log
        verify(logger).info(argThat(msg -> 
            !msg.contains("Authorization") &&
            !msg.contains("Bearer token") &&
            !msg.contains("dado-sensivel")
        ));
        
        // Verifica que headers não sensíveis são logados
        verify(logger).info(argThat(msg -> 
            msg.contains("X-Correlation-Id")
        ));
    }

    /**
     * Verifica se o filtro mede o tempo de processamento.
     */
    @Test
    void deveMedirTempoDeProcessamento() {
        // Simula um atraso na cadeia de filtros
        when(cadeiaFiltros.filter(any(ServerWebExchange.class)))
            .thenReturn(Mono.delay(java.time.Duration.ofMillis(100)).then());

        filtroLogging.filter(exchange, cadeiaFiltros).block();

        // Verifica que a mensagem de response inclui a duração
        verify(logger).info(argThat(msg -> 
            msg.contains("Duration") &&
            msg.contains("ms")
        ));
    }

    /**
     * Verifica a ordem de execução do filtro.
     */
    @Test
    void deveRetornarOrdemCorreta() {
        assertEquals(Ordered.HIGHEST_PRECEDENCE + 1000, filtroLogging.getOrder(),
            "A ordem do filtro deve ser HIGHEST_PRECEDENCE + 1000");
    }

    /**
     * Verifica se o Correlation ID é propagado no contexto.
     */
    @Test
    void devePropagarCorrelationIdNoContexto() {
        // Configura a cadeia para verificar o contexto
        when(cadeiaFiltros.filter(any())).thenAnswer(invocacao -> {
            ServerWebExchange exchange = invocacao.getArgument(0);
            String correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-Id");
            
            return Mono.deferContextual(contextView -> {
                String ctxCorrelationId = contextView.getOrDefault("X-Correlation-Id", "");
                assertEquals(correlationId, ctxCorrelationId,
                    "O Correlation ID deve ser propagado no contexto");
                return Mono.empty();
            });
        });

        filtroLogging.filter(exchange, cadeiaFiltros).block();
    }

    /**
     * Verifica se o filtro registra logs apenas quando o nível INFO está habilitado.
     */
    @Test
    void deveRegistrarLogsApenasQuandoInfoHabilitado() {
        when(logger.isInfoEnabled()).thenReturn(false);

        filtroLogging.filter(exchange, cadeiaFiltros).block();

        verify(logger, never()).info(anyString());
    }

    /**
     * Verifica se o filtro trata corretamente erros durante o processamento.
     */
    @Test
    void deveRegistrarLogsMesmoComErroNoProcessamento() {
        when(cadeiaFiltros.filter(any())).thenReturn(Mono.error(new RuntimeException("Erro simulado")));

        filtroLogging.filter(exchange, cadeiaFiltros).onErrorResume(e -> Mono.empty()).block();

        // Verifica que o log de resposta foi registrado mesmo com erro
        verify(logger).info(argThat(msg -> 
            msg.startsWith("Response [teste-correlation-id]: Status") &&
            msg.contains("Path: /teste")
        ));
    }
}
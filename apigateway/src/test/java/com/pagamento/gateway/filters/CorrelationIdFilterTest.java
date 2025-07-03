package com.pagamento.gateway.filters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Testes para o filtro de Correlation ID.
 * 
 * <p>Verifica o comportamento do filtro em relação a:
 * <ul>
 *   <li>Geração de um novo Correlation ID quando ausente</li>
 *   <li>Propagação de Correlation ID existente</li>
 *   <li>Adição do Correlation ID na resposta</li>
 *   <li>Comportamento em diferentes cenários de requisição</li>
 * </ul>
 */
class CorrelationIdFilterTest {

    private CorrelationIdFilter filtro;

    @BeforeEach
    void configurar() {
        filtro = new CorrelationIdFilter();
    }

    /**
     * Verifica se um novo Correlation ID é gerado e adicionado quando ausente na requisição.
     * 
     * <p>O filtro deve:
     * <ol>
     *   <li>Gerar um novo UUID como Correlation ID</li>
     *   <li>Adicionar o ID ao header da requisição</li>
     *   <li>Adicionar o mesmo ID ao header da resposta</li>
     *   <li>Propagar a requisição modificada</li>
     * </ol>
     */
    @Test
    void deveGerarNovoCorrelationIdQuandoAusente() {
        // Configuração
        ServerWebExchange exchange = criarExchangeSimulado(new HttpHeaders());
        GatewayFilterChain cadeia = mock(GatewayFilterChain.class);
        
        // Execução
        Mono<Void> resultado = filtro.filter(exchange, cadeia);
        
        // Verificação
        StepVerifier.create(resultado).verifyComplete();
        verify(exchange.getResponse().getHeaders()).add(
            eq(CorrelationIdFilter.CORRELATION_ID_HEADER), 
            anyString()
        );
    }

    /**
     * Verifica se um Correlation ID existente é propagado corretamente.
     * 
     * <p>O filtro deve:
     * <ol>
     *   <li>Manter o Correlation ID existente na requisição</li>
     *   <li>Adicionar o mesmo ID ao header da resposta</li>
     *   <li>Não gerar um novo ID</li>
     * </ol>
     */
    @Test
    void devePropagarCorrelationIdExistente() {
        // Configuração
        String idExistente = "test-id-123";
        HttpHeaders headers = new HttpHeaders();
        headers.add(CorrelationIdFilter.CORRELATION_ID_HEADER, idExistente);
        
        ServerWebExchange exchange = criarExchangeSimulado(headers);
        GatewayFilterChain cadeia = mock(GatewayFilterChain.class);
        
        // Execução
        Mono<Void> resultado = filtro.filter(exchange, cadeia);
        
        // Verificação
        StepVerifier.create(resultado).verifyComplete();
        verify(exchange.getResponse().getHeaders()).add(
            CorrelationIdFilter.CORRELATION_ID_HEADER, 
            idExistente
        );
    }

    /**
     * Verifica se o Correlation ID é adicionado mesmo quando ocorre erro na cadeia de filtros.
     * 
     * <p>O filtro deve garantir que o Correlation ID seja adicionado à resposta mesmo em casos de erro.
     */
    @Test
    void deveAdicionarCorrelationIdMesmoComErroNaCadeia() {
        // Configuração
        ServerWebExchange exchange = criarExchangeSimulado(new HttpHeaders());
        GatewayFilterChain cadeia = mock(GatewayFilterChain.class);
        
        when(cadeia.filter(any())).thenReturn(Mono.error(new RuntimeException("Erro simulado")));
        
        // Execução
        Mono<Void> resultado = filtro.filter(exchange, cadeia);
        
        // Verificação
        StepVerifier.create(resultado)
            .expectError(RuntimeException.class)
            .verify();
        
        verify(exchange.getResponse().getHeaders()).add(
            eq(CorrelationIdFilter.CORRELATION_ID_HEADER), 
            anyString()
        );
    }

    /**
     * Verifica se o Correlation ID é único para cada requisição.
     * 
     * <p>O filtro deve gerar um ID diferente para cada requisição quando não fornecido.
     */
    @Test
    void deveGerarIdUnicoParaCadaRequisicao() {
        // Configuração
        ServerWebExchange exchange1 = criarExchangeSimulado(new HttpHeaders());
        ServerWebExchange exchange2 = criarExchangeSimulado(new HttpHeaders());
        GatewayFilterChain cadeia = mock(GatewayFilterChain.class);
        
        // Execução
        filtro.filter(exchange1, cadeia).block();
        filtro.filter(exchange2, cadeia).block();
        
        // Verificação
        String id1 = exchange1.getResponse().getHeaders()
            .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);
        
        String id2 = exchange2.getResponse().getHeaders()
            .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);
        
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);
    }

    /**
     * Verifica o formato do Correlation ID gerado.
     * 
     * <p>O ID gerado deve seguir o padrão UUID.
     */
    @Test
    void deveGerarIdNoFormatoUUID() {
        // Configuração
        ServerWebExchange exchange = criarExchangeSimulado(new HttpHeaders());
        GatewayFilterChain cadeia = mock(GatewayFilterChain.class);
        
        // Execução
        filtro.filter(exchange, cadeia).block();
        
        // Verificação
        String correlationId = exchange.getResponse().getHeaders()
            .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);
        
        assertNotNull(correlationId);
        assertTrue(correlationId.matches(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
        ), "O Correlation ID deve estar no formato UUID");
    }

    // Método auxiliar para criar exchange simulado
    private ServerWebExchange criarExchangeSimulado(HttpHeaders headersRequisicao) {
        // Configuração da requisição
        ServerHttpRequest requisicao = mock(ServerHttpRequest.class);
        when(requisicao.getHeaders()).thenReturn(headersRequisicao);
        
        ServerHttpRequest.Builder construtorRequisicao = mock(ServerHttpRequest.Builder.class);
        when(requisicao.mutate()).thenReturn(construtorRequisicao);
        when(construtorRequisicao.header(anyString(), anyString())).thenReturn(construtorRequisicao);
        when(construtorRequisicao.build()).thenReturn(requisicao);
        
        // Configuração da resposta
        ServerHttpResponse resposta = mock(ServerHttpResponse.class);
        HttpHeaders headersResposta = new HttpHeaders();
        when(resposta.getHeaders()).thenReturn(headersResposta);
        
        // Configuração para beforeCommit
        doAnswer(invocation -> {
            Supplier<Mono<Void>> supplier = invocation.getArgument(0);
            return supplier.get();
        }).when(resposta).beforeCommit(any());
        
        // Configuração do exchange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getRequest()).thenReturn(requisicao);
        when(exchange.getResponse()).thenReturn(resposta);
        
        ServerWebExchange.Builder construtorExchange = mock(ServerWebExchange.Builder.class);
        when(exchange.mutate()).thenReturn(construtorExchange);
        
        // Correção para resolver ambiguidade
        when(construtorExchange.request(any(ServerHttpRequest.class))).thenReturn(construtorExchange);
        
        ServerWebExchange exchangeModificado = mock(ServerWebExchange.class);
        when(construtorExchange.build()).thenReturn(exchangeModificado);
        when(exchangeModificado.getResponse()).thenReturn(resposta);
        
        return exchange;
    }

    // Métodos auxiliares de asserção
    private void assertNotNull(Object object) {
        if (object == null) {
            throw new AssertionError("O objeto não deve ser nulo");
        }
    }
    
    private void assertNotEquals(Object unexpected, Object actual) {
        if (unexpected.equals(actual)) {
            throw new AssertionError("Os objetos não devem ser iguais");
        }
    }
    
    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
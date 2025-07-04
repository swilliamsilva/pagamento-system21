package com.pagamento.gateway.filters;

import org.junit.jupiter.api.AfterEach;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Testes para o filtro de limitação de requisições (Rate Limiting).
 * 
 * <p>Verifica o comportamento do filtro em diferentes cenários:
 * <ul>
 *   <li>Requisições dentro do limite permitido</li>
 *   <li>Bloqueio de requisições que excedem o limite</li>
 *   <li>Utilização de diferentes estratégias para identificar clientes</li>
 *   <li>Headers informativos sobre o estado do rate limit</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class RateLimitingFilterTest {

    @InjectMocks
    private RateLimitingFilter filtroLimitacao;

    @Mock
    private GatewayFilterChain cadeiaFiltros;

    /**
     * Limpa os registros de limitação após cada teste.
     * 
     * <p>Garante isolamento entre os testes, evitando interferência
     * de estado acumulado entre execuções.
     */
    @AfterEach
    void limparEstado() {
        filtroLimitacao.clearBuckets();
    }

    /**
     * Cria um exchange simulado com informações de endereço remoto.
     * 
     * <p>Inclui:
     * <ul>
     *   <li>Endereço IP local (127.0.0.1)</li>
     *   <li>Header X-Forwarded-For com múltiplos IPs</li>
     *   <li>Endpoint /api/recurso para teste</li>
     * </ul>
     */
    private MockServerWebExchange criarExchange() {
        try {
            InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
            InetSocketAddress socketAddress = new InetSocketAddress(inetAddress, 8080);
            
            return MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/recurso")
                    .remoteAddress(socketAddress)
                    .header("X-Forwarded-For", "192.168.1.100, 10.0.0.1")
                    .build()
            );
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifica se requisições dentro do limite são permitidas.
     * 
     * <p>O teste valida:
     * <ul>
     *   <li>Status de resposta não alterado (requisição permitida)</li>
     *   <li>Headers informativos sobre o estado do rate limit</li>
     *   <li>Contagem correta de tokens restantes</li>
     * </ul>
     */
    @Test
    void devePermitirRequisicoesDentroDoLimite() {
        // Configuração
        ServerWebExchange exchange = criarExchange();
        when(cadeiaFiltros.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        // Execução
        Mono<Void> resultado = filtroLimitacao.filter(exchange, cadeiaFiltros);
        
        // Verificação
        StepVerifier.create(resultado).verifyComplete();
            
        // Status não deve ser alterado para requisições permitidas
        assertNull(exchange.getResponse().getStatusCode(), 
            "Status não deve ser definido para requisições dentro do limite");
            
        // Verifica headers informativos
        assertEquals("4", exchange.getResponse().getHeaders().getFirst("X-Rate-Limit-Remaining"),
            "Deveria ter 4 tokens restantes após 1 consumo");
        assertEquals("5", exchange.getResponse().getHeaders().getFirst("X-Rate-Limit-Capacity"),
            "Capacidade total deve ser 5 tokens");
        assertNotNull(exchange.getResponse().getHeaders().getFirst("X-Rate-Limit-Reset"),
            "Deveria conter informação sobre o reset do limite");
    }

    /**
     * Verifica se requisições além do limite são bloqueadas.
     * 
     * <p>O teste valida:
     * <ul>
     *   <li>Status 429 (Too Many Requests) para requisições bloqueadas</li>
     *   <li>Header Retry-After com tempo de espera</li>
     *   <li>Headers informativos sobre o estado do rate limit</li>
     * </ul>
     */
    @Test
    void deveBloquearRequisicoesAcimaDoLimite() {
        // Configuração
        when(cadeiaFiltros.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        // Primeiras 5 requisições (dentro do limite)
        for (int i = 0; i < 5; i++) {
            ServerWebExchange exchange = criarExchange();
            StepVerifier.create(filtroLimitacao.filter(exchange, cadeiaFiltros))
                .verifyComplete();
        }
        
        // 6ª requisição (deve ser bloqueada)
        ServerWebExchange exchangeBloqueado = criarExchange();
        
        // Execução
        Mono<Void> resultado = filtroLimitacao.filter(exchangeBloqueado, cadeiaFiltros);
        
        // Verificação
        StepVerifier.create(resultado).verifyComplete();
            
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, exchangeBloqueado.getResponse().getStatusCode(),
            "Deveria retornar status 429 (Too Many Requests)");
        assertEquals("60", exchangeBloqueado.getResponse().getHeaders().getFirst("Retry-After"),
            "Deveria especificar 60 segundos de espera no header Retry-After");
        assertEquals("0", exchangeBloqueado.getResponse().getHeaders().getFirst("X-Rate-Limit-Remaining"),
            "Deveria indicar 0 tokens restantes");
    }
    
    /**
     * Verifica se o filtro prioriza o header X-Forwarded-For para identificar clientes.
     * 
     * <p>Em ambientes com proxy reverso, o IP real do cliente vem neste header.
     * O filtro deve usar o primeiro IP da lista.
     */
    @Test
    void devePriorizarHeaderXForwardedForParaIdentificacao() {
        // Configuração
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/recurso")
                .header("X-Forwarded-For", "203.0.113.195, 70.41.3.18, 150.172.238.178")
                .build()
        );
        
        when(cadeiaFiltros.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        // Execução
        Mono<Void> resultado = filtroLimitacao.filter(exchange, cadeiaFiltros);
        
        // Verificação
        StepVerifier.create(resultado).verifyComplete();
        
        // Verifica se o bucket foi criado para o IP do X-Forwarded-For
        assertEquals(1, filtroLimitacao.getBucketCount(),
            "Deveria ter criado bucket para o IP 203.0.113.195 do X-Forwarded-For");
    }

    /**
     * Verifica o comportamento quando o header X-Forwarded-For está ausente.
     * 
     * <p>O filtro deve usar o endereço remoto direto quando o header não está presente.
     */
    @Test
    void deveUsarEnderecoRemotoDiretoQuandoXForwardedForAusente() {
        // Configuração
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/recurso")
                .remoteAddress(new InetSocketAddress("192.168.1.150", 8080))
                .build()
        );
        
        when(cadeiaFiltros.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        // Execução
        Mono<Void> resultado = filtroLimitacao.filter(exchange, cadeiaFiltros);
        
        // Verificação
        StepVerifier.create(resultado).verifyComplete();
        
        // Verifica se o bucket foi criado para o IP remoto direto
        assertEquals(1, filtroLimitacao.getBucketCount(),
            "Deveria ter criado bucket para o IP remoto 192.168.1.150");
    }

    /**
     * Verifica se o limite é independente para diferentes clientes.
     * 
     * <p>Requisições de diferentes clientes não devem compartilhar o mesmo contador.
     */
    @Test
    void deveManterContadoresSeparadosParaDiferentesClientes() {
        // Configuração
        when(cadeiaFiltros.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        // Executar 5 requisições do cliente A
        for (int i = 0; i < 5; i++) {
            MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/recurso")
                    .header("X-Forwarded-For", "cliente-A")
                    .build()
            );
            filtroLimitacao.filter(exchange, cadeiaFiltros).block();
        }
        
        // Requisição do cliente B deve ser permitida
        MockServerWebExchange exchangeClienteB = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/recurso")
                .header("X-Forwarded-For", "cliente-B")
                .build()
        );
        
        // Execução
        Mono<Void> resultado = filtroLimitacao.filter(exchangeClienteB, cadeiaFiltros);
        
        // Verificação
        StepVerifier.create(resultado).verifyComplete();
        assertNull(exchangeClienteB.getResponse().getStatusCode(),
            "Requisição do cliente B deve ser permitida");
        assertEquals("4", exchangeClienteB.getResponse().getHeaders().getFirst("X-Rate-Limit-Remaining"),
            "Cliente B deve ter 4 tokens restantes");
    }
}
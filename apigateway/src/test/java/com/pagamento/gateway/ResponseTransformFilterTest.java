package com.pagamento.gateway;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagamento.gateway.filters.ResponseTransformFilter;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Testes unitários para o filtro de transformação de respostas.
 * 
 * <p>Verifica o comportamento do {@link ResponseTransformFilter} em diferentes cenários:
 * <ul>
 *   <li>Transformação de respostas JSON em formato padrão</li>
 *   <li>Manutenção de respostas não-JSON sem modificação</li>
 *   <li>Adição de headers de segurança</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class FiltroTransformacaoRespostaTest {

    @InjectMocks
    private ResponseTransformFilter filtroTransformacao;

    @Mock
    private GatewayFilterChain cadeiaFiltros;

    /**
     * Verifica se o filtro transforma corretamente respostas JSON no formato padrão.
     * 
     * <p>O formato esperado é:
     * <pre>
     * {
     *   "success": true,
     *   "status": 200,
     *   "data": {...},
     *   "timestamp": "2023-01-01T00:00:00Z"
     * }
     * </pre>
     */
    @Test
    void deveTransformarRespostaJsonParaFormatoPadrao() throws Exception {
        // Configuração
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/dados").build()
        );

        MockServerHttpResponse resposta = exchange.getResponse();
        resposta.setStatusCode(HttpStatus.OK);
        resposta.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Comportamento simulado da cadeia de filtros
        when(cadeiaFiltros.filter(any(ServerWebExchange.class)))
            .thenAnswer(invocacao -> {
                ServerWebExchange ex = invocacao.getArgument(0);
                String jsonOriginal = "{\"nome\":\"João\", \"idade\":30}";
                DataBuffer buffer = new DefaultDataBufferFactory().wrap(jsonOriginal.getBytes());
                return ex.getResponse().writeWith(Flux.just(buffer));
            });

        // Execução
        Mono<Void> resultado = filtroTransformacao.filter(exchange, cadeiaFiltros);

        // Verificação
        StepVerifier.create(resultado)
            .verifyComplete();

        String corpoTransformado = obterCorpoRespostaComoString(resposta);
        assertNotNull(corpoTransformado, "O corpo da resposta não deve ser nulo");

        ObjectMapper mapeador = new ObjectMapper();
        JsonNode json = mapeador.readTree(corpoTransformado);

        // Valida estrutura do JSON transformado
        assertTrue(json.get("success").asBoolean(), "O campo 'success' deve ser true");
        assertEquals(HttpStatus.OK.value(), json.get("status").asInt(), "Status deve ser 200");
        assertNotNull(json.get("data"), "O campo 'data' deve estar presente");
        assertNotNull(json.get("timestamp"), "O campo 'timestamp' deve estar presente");

        // Valida headers de segurança
        assertEquals("nosniff", resposta.getHeaders().getFirst("X-Content-Type-Options"),
            "Header X-Content-Type-Options deve ser 'nosniff'");
        assertEquals("DENY", resposta.getHeaders().getFirst("X-Frame-Options"),
            "Header X-Frame-Options deve ser 'DENY'");
        assertEquals("default-src 'self'", resposta.getHeaders().getFirst("Content-Security-Policy"),
            "Header Content-Security-Policy deve ser 'default-src 'self''");
    }

    /**
     * Verifica se o filtro mantém respostas não-JSON inalteradas.
     * 
     * <p>O filtro não deve modificar respostas de tipos como:
     * - text/plain
     * - application/xml
     * - application/octet-stream
     */
    @Test
    void naoDeveTransformarRespostasNaoJson() {
        // Configuração
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/texto").build()
        );

        MockServerHttpResponse resposta = exchange.getResponse();
        resposta.setStatusCode(HttpStatus.OK);
        resposta.getHeaders().setContentType(MediaType.TEXT_PLAIN);

        String corpoResposta = "Conteúdo textual simples";
        DataBuffer buffer = new DefaultDataBufferFactory().wrap(corpoResposta.getBytes());

        when(cadeiaFiltros.filter(any(ServerWebExchange.class)))
            .thenAnswer(invocacao -> {
                ServerWebExchange ex = invocacao.getArgument(0);
                return ex.getResponse().writeWith(Flux.just(buffer));
            });

        // Execução
        Mono<Void> resultado = filtroTransformacao.filter(exchange, cadeiaFiltros);

        // Verificação
        StepVerifier.create(resultado)
            .verifyComplete();

        String corpo = obterCorpoRespostaComoString(resposta);
        assertEquals(corpoResposta, corpo, "O corpo da resposta deve permanecer inalterado");
    }

    /**
     * Verifica se o filtro adiciona headers de segurança mesmo em respostas não transformadas.
     */
    @Test
    void deveAdicionarHeadersSegurancaEmTodasRespostas() {
        // Configuração
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/seguro").build()
        );

        MockServerHttpResponse resposta = exchange.getResponse();
        resposta.setStatusCode(HttpStatus.OK);
        resposta.getHeaders().setContentType(MediaType.TEXT_PLAIN);

        when(cadeiaFiltros.filter(any(ServerWebExchange.class)))
            .thenAnswer(invocacao -> {
                ServerWebExchange ex = invocacao.getArgument(0);
                return ex.getResponse().writeWith(Flux.empty());
            });

        // Execução
        Mono<Void> resultado = filtroTransformacao.filter(exchange, cadeiaFiltros);

        // Verificação
        StepVerifier.create(resultado)
            .verifyComplete();

        // Valida headers de segurança
        assertNotNull(resposta.getHeaders().getFirst("X-Content-Type-Options"),
            "Header de segurança X-Content-Type-Options deve estar presente");
        assertNotNull(resposta.getHeaders().getFirst("X-Frame-Options"),
            "Header de segurança X-Frame-Options deve estar presente");
        assertNotNull(resposta.getHeaders().getFirst("Content-Security-Policy"),
            "Header de segurança Content-Security-Policy deve estar presente");
    }

    /**
     * Método auxiliar para obter o corpo da resposta como string.
     * 
     * @param resposta A resposta HTTP simulada
     * @return O conteúdo do corpo como string
     */
    private String obterCorpoRespostaComoString(MockServerHttpResponse resposta) {
        List<DataBuffer> buffers = resposta.getBody()
            .collectList()
            .block();

        if (buffers == null || buffers.isEmpty()) return null;

        DataBuffer bufferCombinado = new DefaultDataBufferFactory().join(buffers);
        byte[] bytes = new byte[bufferCombinado.readableByteCount()];
        bufferCombinado.read(bytes);

        // Liberar recursos
        buffers.forEach(DataBufferUtils::release);
        DataBufferUtils.release(bufferCombinado);

        return new String(bytes, StandardCharsets.UTF_8);
    }
}
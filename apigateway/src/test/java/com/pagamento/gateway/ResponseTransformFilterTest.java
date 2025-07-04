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

@ExtendWith(MockitoExtension.class)
class ResponseTransformFilterTest {

    @InjectMocks
    private ResponseTransformFilter filtroTransformacao;

    @Mock
    private GatewayFilterChain cadeiaFiltros;

    @Test
    void deveTransformarRespostaJsonParaFormatoPadrao() throws Exception {
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/dados").build()
        );

        MockServerHttpResponse resposta = exchange.getResponse();
        resposta.setStatusCode(HttpStatus.OK);
        resposta.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        when(cadeiaFiltros.filter(any(ServerWebExchange.class)))
            .thenAnswer(invocacao -> {
                ServerWebExchange ex = invocacao.getArgument(0);
                String jsonOriginal = "{\"nome\":\"João\", \"idade\":30}";
                DataBuffer buffer = new DefaultDataBufferFactory().wrap(jsonOriginal.getBytes());
                return ex.getResponse().writeWith(Flux.just(buffer));
            });

        Mono<Void> resultado = filtroTransformacao.filter(exchange, cadeiaFiltros);
        StepVerifier.create(resultado).verifyComplete();

        String corpoTransformado = obterCorpoRespostaComoString(resposta);
        assertNotNull(corpoTransformado);

        ObjectMapper mapeador = new ObjectMapper();
        JsonNode json = mapeador.readTree(corpoTransformado);

        assertTrue(json.get("success").asBoolean());
        assertEquals(HttpStatus.OK.value(), json.get("status").asInt());
        assertNotNull(json.get("data"));
        assertNotNull(json.get("timestamp"));

        assertEquals("nosniff", resposta.getHeaders().getFirst("X-Content-Type-Options"));
        assertEquals("DENY", resposta.getHeaders().getFirst("X-Frame-Options"));
        assertEquals("default-src 'self'", resposta.getHeaders().getFirst("Content-Security-Policy"));
    }

    @Test
    void naoDeveTransformarRespostasNaoJson() {
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

        Mono<Void> resultado = filtroTransformacao.filter(exchange, cadeiaFiltros);
        StepVerifier.create(resultado).verifyComplete();

        String corpo = obterCorpoRespostaComoString(resposta);
        assertEquals(corpoResposta, corpo);

        assertEquals("nosniff", resposta.getHeaders().getFirst("X-Content-Type-Options"));
        assertEquals("DENY", resposta.getHeaders().getFirst("X-Frame-Options"));
        assertEquals("default-src 'self'", resposta.getHeaders().getFirst("Content-Security-Policy"));
    }

    @Test
    void deveAdicionarHeadersSegurancaEmTodasRespostas() {
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

        Mono<Void> resultado = filtroTransformacao.filter(exchange, cadeiaFiltros);
        StepVerifier.create(resultado).verifyComplete();

        assertNotNull(resposta.getHeaders().getFirst("X-Content-Type-Options"));
        assertNotNull(resposta.getHeaders().getFirst("X-Frame-Options"));
        assertNotNull(resposta.getHeaders().getFirst("Content-Security-Policy"));
    }

    private String obterCorpoRespostaComoString(MockServerHttpResponse resposta) {
        List<DataBuffer> buffers = resposta.getBody().collectList().block();
        if (buffers == null || buffers.isEmpty()) return null;

        DataBuffer bufferCombinado = new DefaultDataBufferFactory().join(buffers);
        byte[] bytes = new byte[bufferCombinado.readableByteCount()];
        bufferCombinado.read(bytes);

        buffers.forEach(DataBufferUtils::release);
        DataBufferUtils.release(bufferCombinado);

        return new String(bytes, StandardCharsets.UTF_8);
    }
}

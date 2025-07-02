package com.pagamento.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagamento.gateway.filters.ResponseTransformFilter;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ResponseTransformFilterTest {

    @InjectMocks
    private ResponseTransformFilter responseTransformFilter;

    @Mock
    private GatewayFilterChain filterChain;

    @Test
    void shouldTransformJsonResponse() throws Exception {
        // Arrange
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/data").build()
        );
        
        MockServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        // Simula um corpo de resposta JSON
        String responseBody = "{\"name\":\"John\", \"age\":30}";
        DataBuffer buffer = new DefaultDataBufferFactory().wrap(responseBody.getBytes());
        
        // Configura o comportamento do filterChain para escrever a resposta
        when(filterChain.filter(exchange)).thenReturn(
            Mono.defer(() -> response.writeWith(Flux.just(buffer)))
        );
        
        // Act
        Mono<Void> result = responseTransformFilter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
        
        // Obtém o corpo da resposta transformada
        String transformedBody = getResponseBodyAsString(response);
        assertNotNull(transformedBody);
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(transformedBody);
        
        assertTrue(json.get("success").asBoolean());
        assertEquals(HttpStatus.OK.value(), json.get("status").asInt());
        assertNotNull(json.get("data"));
        assertNotNull(json.get("timestamp"));
        
        // Verifica headers de segurança
        assertEquals("nosniff", response.getHeaders().getFirst("X-Content-Type-Options"));
        assertEquals("DENY", response.getHeaders().getFirst("X-Frame-Options"));
        assertEquals("default-src 'self'", response.getHeaders().getFirst("Content-Security-Policy"));
    }

    @Test
    void shouldNotTransformNonJsonResponse() {
        // Arrange
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/data").build()
        );
        
        MockServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
        
        // Simula um corpo de resposta de texto
        String responseBody = "Hello World";
        DataBuffer buffer = new DefaultDataBufferFactory().wrap(responseBody.getBytes());
        
        when(filterChain.filter(exchange)).thenReturn(
            Mono.defer(() -> response.writeWith(Flux.just(buffer)))
        );
        
        // Act
        Mono<Void> result = responseTransformFilter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
            .verifyComplete();
        
        // Não deve ter modificado o corpo
        String body = getResponseBodyAsString(response);
        assertEquals("Hello World", body);
    }

    // Método auxiliar para extrair o corpo da resposta
    private String getResponseBodyAsString(MockServerHttpResponse response) {
        // Coletar todos os buffers em uma lista
        List<DataBuffer> buffers = response.getBody()
            .collectList()
            .block();
        
        if (buffers == null || buffers.isEmpty()) {
            return null;
        }
        
        // Juntar todos os buffers em um único buffer
        DataBuffer joined = new DefaultDataBufferFactory().join(buffers);
        byte[] bytes = new byte[joined.readableByteCount()];
        joined.read(bytes);
        
        // Liberar os buffers
        buffers.forEach(DataBufferUtils::release);
        DataBufferUtils.release(joined);
        
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
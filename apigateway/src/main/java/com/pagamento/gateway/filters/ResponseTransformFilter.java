package com.pagamento.gateway.filters;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ResponseTransformFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Clona o exchange original para capturar a resposta
        ServerWebExchange modifiedExchange = exchange.mutate()
            .response(new CapturingResponseDecorator(exchange.getResponse()))
            .build();
        
        return chain.filter(modifiedExchange).then(Mono.defer(() -> {
            ServerHttpResponse response = exchange.getResponse();
            CapturingResponseDecorator capturedResponse = (CapturingResponseDecorator) modifiedExchange.getResponse();
            
            // 1. Aplica apenas para JSON
            MediaType contentType = capturedResponse.getContentType();
            if (contentType != null && contentType.includes(MediaType.APPLICATION_JSON)) {
                
                // Adiciona headers de segurança SEMPRE
                response.getHeaders().add("X-Content-Type-Options", "nosniff");
                response.getHeaders().add("X-Frame-Options", "DENY");
                response.getHeaders().add("Content-Security-Policy", "default-src 'self'");
                
                // Processa transformação
                String transformedBody = transformResponse(
                    capturedResponse.getBodyAsString(), 
                    response.getStatusCode()
                );
                
                // Prepara novo conteúdo
                byte[] bytes = transformedBody.getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = response.bufferFactory().wrap(bytes);
                
                // Atualiza headers
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                response.getHeaders().setContentLength(bytes.length);
                
                return response.writeWith(Mono.just(buffer));
            }
            return Mono.empty();
        }));
    }

    private String transformResponse(String originalBody, HttpStatusCode statusCode) {
        try {
            Map<String, Object> transformed = new HashMap<>();
            int status = statusCode != null ? statusCode.value() : 500;
            transformed.put("status", status);
            transformed.put("success", statusCode != null && statusCode.is2xxSuccessful());
            
            // Parse do corpo original se existir
            Object data = null;
            if (originalBody != null && !originalBody.isEmpty()) {
                data = objectMapper.readValue(originalBody, Object.class);
            }
            
            transformed.put("data", data);
            transformed.put("timestamp", System.currentTimeMillis());
            
            return objectMapper.writeValueAsString(transformed);
        } catch (Exception e) {
            return "{\"error\":\"Erro na transformação da resposta\"}";
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1; // Executar depois de outros filtros
    }
    
    // Classe auxiliar para capturar o corpo da resposta
    static class CapturingResponseDecorator extends ServerHttpResponseDecorator {
        private final StringBuilder body = new StringBuilder();
        
        public CapturingResponseDecorator(ServerHttpResponse delegate) {
            super(delegate);
        }
        
        public MediaType getContentType() {
            return getHeaders().getContentType();
        }
        
        @Override
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> bodyPublisher) {
            return Flux.from(bodyPublisher)
                .doOnNext(dataBuffer -> {
                    try {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        this.body.append(new String(bytes, StandardCharsets.UTF_8));
                    } finally {
                        DataBufferUtils.release(dataBuffer); // Libera o buffer
                    }
                })
                .then(); // Não escreve no delegate
        }
        
        @Override
        public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
            return writeWith(Flux.from(body).flatMapSequential(p -> p));
        }
        
        public String getBodyAsString() {
            return body.toString();
        }
    }
}
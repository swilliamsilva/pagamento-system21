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
        ServerHttpResponse originalResponse = exchange.getResponse();
        CapturingResponseDecorator decoratedResponse = new CapturingResponseDecorator(originalResponse);

        ServerWebExchange modifiedExchange = exchange.mutate().response(decoratedResponse).build();

        return chain.filter(modifiedExchange).then(Mono.defer(() -> {
            MediaType contentType = decoratedResponse.getContentType();
            addSecurityHeaders(originalResponse);

            if (contentType != null && contentType.includes(MediaType.APPLICATION_JSON)) {
                String transformedBody = transformResponse(
                    decoratedResponse.getBodyAsString(),
                    originalResponse.getStatusCode()
                );

                byte[] bytes = transformedBody.getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = originalResponse.bufferFactory().wrap(bytes);

                originalResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                originalResponse.getHeaders().setContentLength(bytes.length);

                return originalResponse.writeWith(Mono.just(buffer));
            }

            // Se não for JSON, escreve corpo original (caso exista)
            String originalBody = decoratedResponse.getBodyAsString();
            if (originalBody != null && !originalBody.isEmpty()) {
                byte[] bytes = originalBody.getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = originalResponse.bufferFactory().wrap(bytes);
                return originalResponse.writeWith(Mono.just(buffer));
            }

            return originalResponse.setComplete();
        }));
    }

    private void addSecurityHeaders(ServerHttpResponse response) {
        response.getHeaders().add("X-Content-Type-Options", "nosniff");
        response.getHeaders().add("X-Frame-Options", "DENY");
        response.getHeaders().add("Content-Security-Policy", "default-src 'self'");
    }

    private String transformResponse(String originalBody, HttpStatusCode statusCode) {
        try {
            Map<String, Object> transformed = new HashMap<>();
            int status = statusCode != null ? statusCode.value() : 500;
            transformed.put("status", status);
            transformed.put("success", statusCode != null && statusCode.is2xxSuccessful());

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
        return Ordered.LOWEST_PRECEDENCE - 1;
    }

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
                        DataBufferUtils.release(dataBuffer);
                    }
                })
                .then();
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

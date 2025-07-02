package com.pagamento.gateway.filters;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.profiles.active=test-local-rate-limit"
)
class LocalRateLimitingTest {  // Removido modificador 'public'

    @Autowired
    private RateLimitingFilter rateLimitingFilter;

    @Autowired
    private WebTestClient webTestClient;

    @AfterEach
    void cleanup() {  // Removido modificador 'public'
        rateLimitingFilter.clearBuckets();
    }

    @Test
    void shouldBlockAfterLimit() {
        // Primeiras 5 requisições (limite) devem passar
        for (int i = 0; i < 5; i++) {
            webTestClient.get()
                .uri("/api/limited")
                .exchange()
                .expectStatus().isOk();
        }

        // 6ª requisição deve ser bloqueada
        webTestClient.get()
            .uri("/api/limited")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)  // Usando HttpStatus do Spring
            .expectHeader().valueEquals("Retry-After", "60");
    }
}
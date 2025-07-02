package com.pagamento.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.profiles.active=test-rate-limit-block"
)
class RedisRateLimitingTest {  // Removido modificador 'public'

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldBlockAfterLimit() {
        // Primeiras 5 requisições (dentro do limite) devem passar
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
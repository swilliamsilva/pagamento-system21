package com.pagamento.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Teste para verificar o comportamento quando o Redis está indisponível.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("no-redis")  // Perfil aplicado a nível de classe
class RedisRateLimitingNoRedisTest {

    @Autowired
    private WebTestClient webTestClient;

    /**
     * Verifica se o sistema permite requisições mesmo com Redis indisponível.
     */
    @Test
    void devePermitirRequisicoesQuandoRedisIndisponivel() {
        webTestClient.get()
            .uri("/api/protegido")
            .exchange()
            .expectStatus().isOk();
    }
}
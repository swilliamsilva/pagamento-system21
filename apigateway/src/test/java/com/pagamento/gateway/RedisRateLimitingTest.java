package com.pagamento.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.profiles.active=test-rate-limit-block")
public class RedisRateLimitingTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldBlockAfterLimit() {
        for (int i = 0; i < 5; i++) {
            webTestClient.get()
                .uri("/api/limited")
                .exchange()
                .expectStatus().isOk();
        }
        webTestClient.get()
            .uri("/api/limited")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SC_TOO_MANY_REQUESTS)
            .expectHeader().valueEquals("Retry-After", "60");
    }
}

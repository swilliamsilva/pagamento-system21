package com.pagamento.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

/**
 * Testes de integração para validação do limitador de requisições baseado em Redis.
 * 
 * <p>Configurações testadas (application.yml):
 * <ul>
 *   <li>filters.rate-limiting.capacity: 5</li>
 *   <li>filters.rate-limiting.refill-period: 60</li>
 *   <li>spring.cloud.gateway.filter.request-rate-limiter.redis-rate-limiter</li>
 * </ul>
 * 
 * <p>O teste utiliza o perfil "test-rate-limit" para habilitar as configurações
 * específicas de rate limiting durante os testes.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-rate-limit")
@Testcontainers
class RedisRateLimitingTest {

    @Autowired
    private WebTestClient webTestClient;
    
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.2.5")
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @BeforeEach
    void limparEstadoRedis() {
        try (RedisConnection conexao = redisConnectionFactory.getConnection()) {
            conexao.serverCommands().flushDb();
        }
    }

    /**
     * Verifica se requisições além do limite configurado são bloqueadas.
     * 
     * <p>Fluxo do teste:
     * <ol>
     *   <li>Executa 4 requisições bem-sucedidas</li>
     *   <li>Verifica que a 5ª requisição é permitida (limite máximo)</li>
     *   <li>Confirma que a 6ª requisição é bloqueada com status 429</li>
     *   <li>Valida o cabeçalho "Retry-After" com valor de 60 segundos</li>
     * </ol>
     */
    @Test
    void deveBloquearRequisicoesAposExcederLimite() {
        // Executar 4 requisições dentro do limite
        for (int i = 0; i < 4; i++) {
            webTestClient.get()
                .uri("/api/protegido")
                .exchange()
                .expectStatus().isOk();
        }

        // 5ª requisição (limite máximo permitido)
        webTestClient.get()
            .uri("/api/protegido")
            .exchange()
            .expectStatus().isOk();

        // 6ª requisição (deve ser bloqueada)
        webTestClient.get()
            .uri("/api/protegido")
            .exchange()
            .expectStatus().isEqualTo(TOO_MANY_REQUESTS)
            .expectHeader().valueEquals("Retry-After", "60");
    }

    /**
     * Verifica o comportamento do sistema após o período de recarga.
     * 
     * <p>Este teste é marcado como lento devido ao tempo de espera
     * e deve ser executado separadamente dos testes rápidos.
     * 
     * <p>Fluxo:
     * <ol>
     *   <li>Excede o limite de requisições</li>
     *   <li>Aguarda o período de recarga + tolerância</li>
     *   <li>Verifica que novas requisições são permitidas após o reset</li>
     * </ol>
     */
    @Test
    @Tag("slow")  // Usando a anotação Tag padrão do JUnit
    void deveResetarContadorAposPeriodoDeRecarga() throws InterruptedException {
        // Exceder o limite
        for (int i = 0; i < 5; i++) {
            webTestClient.get()
                .uri("/api/protegido")
                .exchange()
                .expectStatus().isOk();
        }

        // Verificar bloqueio
        webTestClient.get()
            .uri("/api/protegido")
            .exchange()
            .expectStatus().isEqualTo(TOO_MANY_REQUESTS);

        // Aguardar recarga (60s + 5s de tolerância)
        Thread.sleep(65000);

        // Nova requisição deve ser permitida
        webTestClient.get()
            .uri("/api/protegido")
            .exchange()
            .expectStatus().isOk();
    }
}
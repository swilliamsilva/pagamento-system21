package com.pagamento.gateway.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração para verificar o ambiente Docker com Redis.
 * 
 * <p>Este teste valida a configuração do ambiente de testes com Redis em container Docker,
 * garantindo que a integração entre a aplicação e o Redis está funcionando corretamente.</p>
 * 
 * <p>Verificações realizadas:
 * <ul>
 *   <li>Disponibilidade do bean RedisConnectionFactory no contexto Spring</li>
 *   <li>Conexão funcional com a instância do Redis em container</li>
 *   <li>Operações básicas de comunicação com o Redis</li>
 * </ul>
 * 
 * <p>Utiliza Testcontainers para criar uma instância do Redis durante a execução dos testes.
 */
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
class DockerRedisEnvironmentCheckTest { // Removido modificador 'public'

    @Container
    static GenericContainer<?> containerRedis = new GenericContainer<>(DockerImageName.parse("redis:7.2.5"))
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void configurarPropriedadesRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", containerRedis::getHost);
        registry.add("spring.data.redis.port", containerRedis::getFirstMappedPort);
    }

    @Autowired
    private RedisConnectionFactory fabricaConexaoRedis;

    /**
     * Verifica a integração com o Redis em ambiente Docker.
     * 
     * <p>Este teste garante que:
     * <ol>
     *   <li>O Spring Boot configurou corretamente o RedisConnectionFactory</li>
     *   <li>A aplicação consegue estabelecer conexão com o Redis em container</li>
     *   <li>Operações básicas como PING funcionam corretamente</li>
     * </ol>
     */
    @Test
    void deveValidarConexaoComRedisEmContainerDocker() {
        // Verifica se o bean de conexão foi injetado corretamente
        assertNotNull(fabricaConexaoRedis, 
            "O bean RedisConnectionFactory deve estar disponível no contexto Spring");
        
        // Testa a comunicação com o Redis
        assertDoesNotThrow(() -> {
            try (var conexao = fabricaConexaoRedis.getConnection()) {
                String resposta = conexao.ping();
                assertEquals("PONG", resposta, 
                    "O comando PING deve retornar 'PONG' para conexões saudáveis");
            }
        }, "Falha na comunicação com o Redis em container Docker");
    }
    
    /**
     * Verifica a configuração do container Redis.
     * 
     * <p>Garante que o container está em execução e com as portas mapeadas corretamente.
     */
    @Test
    void deveConfigurarContainerRedisCorretamente() {
        assertTrue(containerRedis.isRunning(), "O container Redis deve estar em execução");
        assertNotNull(containerRedis.getHost());
        assertTrue(containerRedis.getFirstMappedPort() > 0,
            "A porta do Redis deve estar mapeada e disponível");
    }
}
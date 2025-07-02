package com.pagamento.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RedisRateLimitTest { // Removido modificador 'public'

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.0-alpine"))
        .withExposedPorts(6379);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate; // Injetando RedisTemplate para testar conexão

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @Test
    void testRedisConnection() {
        // Teste básico de conexão com Redis
        String testKey = "ratelimit:test";
        String testValue = "success";
        
        redisTemplate.opsForValue().set(testKey, testValue);
        String retrievedValue = (String) redisTemplate.opsForValue().get(testKey);
        
        assertEquals(testValue, retrievedValue, "Deveria recuperar o valor armazenado no Redis");
    }

    @Test
    void testRateLimitingIntegration() {
        // Teste de integração do sistema de rate limiting
        // (Implementação real dependerá da sua lógica de rate limiting)
        assertNotNull(redisTemplate, "RedisTemplate deve ser injetado corretamente");
        
        // Exemplo de verificação de comportamento do rate limiting
        boolean isOperational = redisTemplate.getConnectionFactory()
            .getConnection()
            .ping()
            .equals("PONG");
        
        assertTrue(isOperational, "A conexão com Redis deve estar operacional");
    }
}
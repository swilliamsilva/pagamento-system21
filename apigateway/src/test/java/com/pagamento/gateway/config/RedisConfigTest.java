package com.pagamento.gateway.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@Configuration
@Profile("test-rate-limit-block")
@Testcontainers
public class RedisConfigTest {

    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>(
        DockerImageName.parse("redis:7.0-alpine"))
        .withExposedPorts(6379)
        .withStartupTimeout(Duration.ofSeconds(30));
    
    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return new LettuceConnectionFactory(
            redisContainer.getHost(), 
            redisContainer.getFirstMappedPort()
        );
    }

    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
        ReactiveRedisConnectionFactory factory
    ) {
        return new ReactiveRedisTemplate<>(factory, RedisSerializationContext.string());
    }
    
    @SpringBootTest
    @ActiveProfiles("test-rate-limit-block")
    @Testcontainers
    static class RedisConfigIntegrationTest {
        
        @Autowired
        private ReactiveRedisConnectionFactory connectionFactory;
        
        @Autowired
        private ReactiveRedisTemplate<String, String> redisTemplate;
        
        @Test
        void deveEstabelecerConexaoComRedis() {
            ReactiveRedisConnection connection = connectionFactory.getReactiveConnection();
            
            StepVerifier.create(connection.ping())
                .expectNext("PONG")
                .verifyComplete();
        }
        
        @Test
        void deveExecutarOperacoesBasicasNoRedis() {
            String chave = "teste:chave";
            String valor = "valor-teste";
            
            Mono<Void> operacao = redisTemplate.opsForValue().set(chave, valor)
                .then(redisTemplate.opsForValue().get(chave))
                .flatMap(resultado -> {
                    assertEquals(valor, resultado);
                    return redisTemplate.delete(chave);
                })
                .then();
            
            StepVerifier.create(operacao)
                .verifyComplete();
        }
    }
    
    @SpringBootTest
    @ActiveProfiles("test-rate-limit-block")
    static class RedisConfigUnitTest {
        
        @Autowired
        private ApplicationContext context;
        
        @Test
        void deveCriarBeanReactiveRedisConnectionFactory() {
            ReactiveRedisConnectionFactory factory = 
                context.getBean(ReactiveRedisConnectionFactory.class);
            
            assertNotNull(factory);
            assertInstanceOf(LettuceConnectionFactory.class, factory);
        }
        
        @Test
        void deveCriarBeanReactiveRedisTemplate() {
            ReactiveRedisTemplate<String, String> template = 
                context.getBean(ReactiveRedisTemplate.class);
            
            assertNotNull(template);
        }
    }
    
    @SpringBootTest
    @ActiveProfiles("test-rate-limit-block")
    static class RedisFailureTest {
        
        @MockBean
        private ReactiveRedisConnectionFactory mockConnectionFactory;
        
        @Autowired
        private ReactiveRedisTemplate<String, String> redisTemplate;
        
        @Test
        void deveLidarComFalhaNaConexao() {
            when(mockConnectionFactory.getReactiveConnection())
                .thenThrow(new RuntimeException("Erro simulado de conexão"));
            
            StepVerifier.create(redisTemplate.opsForValue().get("qualquer-chave"))
                .expectError(RuntimeException.class)
                .verify();
        }
    }
}
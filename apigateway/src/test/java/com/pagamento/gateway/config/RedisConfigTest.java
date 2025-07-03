package com.pagamento.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * Configuração do Redis para testes de limitação de requisições com bloqueio.
 * 
 * <p>Esta configuração é ativada apenas quando o perfil "test-rate-limit-block" está ativo
 * e fornece os beans necessários para integração com Redis em ambiente de teste.</p>
 * 
 * <p>Principais componentes configurados:
 * <ul>
 *   <li>Conexão reativa com servidor Redis local</li>
 *   <li>Template Redis reativo para operações de chave-valor</li>
 * </ul>
 * 
 * <p>Utilizada especificamente para testes que exigem controle preciso sobre o estado do Redis
 * e comportamento de bloqueio do rate limiting.
 */
@Configuration
@Profile("test-rate-limit-block")
public class RedisConfigTest {

    /**
     * Configura a fábrica de conexões reativas com Redis.
     * 
     * <p>Estabelece conexão com instância local do Redis na porta padrão 6379.
     * 
     * @return Fábrica de conexões reativas configurada para ambiente de teste
     */
    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    /**
     * Configura o template Redis reativo para operações de string.
     * 
     * <p>Fornece um cliente reativo para interagir com o Redis usando serialização de strings.
     * 
     * @param factory Fábrica de conexões injetada automaticamente
     * @return Template Redis reativo pronto para operações de chave-valor
     */
    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisTemplate<>(factory, RedisSerializationContext.string());
    }
}
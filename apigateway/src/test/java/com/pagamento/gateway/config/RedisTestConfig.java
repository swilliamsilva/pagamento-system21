package com.pagamento.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Configuração do Redis para ambiente de testes.
 * 
 * <p>Esta classe fornece a configuração básica do template Redis para operações síncronas
 * durante a execução de testes. O template configurado permite a interação com o Redis
 * para operações de CRUD e gerenciamento de dados.</p>
 * 
 * <p>Principais características:
 * <ul>
 *   <li>Configura um RedisTemplate para tipos String:Object</li>
 *   <li>Utiliza a fábrica de conexões fornecida pelo Spring Boot</li>
 *   <li>Não requer serializadores customizados para operações básicas</li>
 * </ul>
 */
@Configuration
public class RedisTestConfig {

    /**
     * Configura o template Redis para operações síncronas.
     * 
     * <p>Este bean permite:
     * <ul>
     *   <li>Armazenar e recuperar dados no Redis</li>
     *   <li>Executar operações de limpeza de dados entre testes</li>
     *   <li>Manipular estruturas de dados como strings, hashes, lists, sets</li>
     * </ul>
     * 
     * @param connectionFactory Fábrica de conexões com Redis (injetada automaticamente)
     * @return Template Redis configurado e pronto para uso
     */
    @Bean
    public RedisTemplate<String, Object> configurarTemplateRedis(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
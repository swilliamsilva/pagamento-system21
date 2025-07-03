package com.pagamento.gateway;

import org.junit.jupiter.api.BeforeEach;
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

/**
 * Testes de integração para verificar a conexão e operações básicas do Redis
 * utilizadas pelo sistema de rate limiting.
 * 
 * <p>Estes testes garantem que:
 * <ul>
 *   <li>A configuração do Redis está correta</li>
 *   <li>A conexão com o container Redis está funcionando</li>
 *   <li>Operações básicas de leitura/escrita estão funcionando</li>
 *   <li>O Redis está operacional para suportar o rate limiting</li>
 * </ul>
 * 
 * <p>Utiliza Testcontainers para criar uma instância isolada do Redis durante os testes.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TesteConexaoRedisRateLimit {

    @Container
    static GenericContainer<?> containerRedis = new GenericContainer<>(DockerImageName.parse("redis:7.0-alpine"))
        .withExposedPorts(6379);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @DynamicPropertySource
    static void configurarPropriedadesRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", containerRedis::getHost);
        registry.add("spring.redis.port", () -> containerRedis.getMappedPort(6379));
    }

    /**
     * Prepara o ambiente antes de cada teste limpando o Redis.
     * Garante isolamento entre os testes.
     */
    @BeforeEach
    void prepararAmbiente() {
        if (redisTemplate != null) {
            redisTemplate.getConnectionFactory().getConnection().flushDb();
        }
    }

    /**
     * Verifica operações básicas de escrita e leitura no Redis.
     * 
     * <p>Passos do teste:
     * <ol>
     *   <li>Armazena um valor no Redis usando uma chave específica</li>
     *   <li>Recupera o valor armazenado usando a mesma chave</li>
     *   <li>Verifica se o valor recuperado é igual ao valor armazenado</li>
     * </ol>
     */
    @Test
    void deveArmazenarERecuperarDadosNoRedis() {
        // Dados de teste
        String chave = "ratelimit:teste-conexao";
        String valor = "sucesso";
        
        // Armazena o valor
        redisTemplate.opsForValue().set(chave, valor);
        
        // Recupera o valor
        String valorRecuperado = (String) redisTemplate.opsForValue().get(chave);
        
        // Verifica o resultado
        assertEquals(valor, valorRecuperado, "O valor armazenado deve ser igual ao valor recuperado");
    }

    /**
     * Verifica a conectividade e operacionalidade do Redis.
     * 
     * <p>Este teste confirma que:
     * <ol>
     *   <li>A conexão com o Redis está estabelecida</li>
     *   <li>O Redis responde a comandos básicos</li>
     *   <li>A instância está pronta para uso pelo sistema de rate limiting</li>
     * </ol>
     */
    @Test
    void deveEstabelecerConexaoOperacionalComRedis() {
        assertNotNull(redisTemplate, "RedisTemplate deve ser injetado pelo Spring");
        
        // Verifica se o Redis está respondendo
        String resposta = redisTemplate.getConnectionFactory()
            .getConnection()
            .ping();
        
        assertEquals("PONG", resposta, "O Redis deve responder com PONG para o comando PING");
    }
    
    /**
     * Verifica se o ambiente de testes está configurado corretamente com o Testcontainers.
     * 
     * <p>Confirma que:
     * <ol>
     *   <li>O container Redis está em execução</li>
     *   <li>As propriedades dinâmicas foram configuradas corretamente</li>
     *   <li>As portas estão mapeadas adequadamente</li>
     * </ol>
     */
    @Test
    void deveConfigurarContainerRedisCorretamente() {
        assertTrue(containerRedis.isRunning(), "O container Redis deve estar em execução");
        assertNotNull(containerRedis.getHost());
        assertTrue(containerRedis.getMappedPort(6379) > 0);
    }
}
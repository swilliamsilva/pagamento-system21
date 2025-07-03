package com.pagamento.gateway.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
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
 * Teste de sanidade do ambiente de infraestrutura.
 * 
 * <p>Este teste verifica aspectos fundamentais da configuração do ambiente:
 * <ul>
 *   <li>Integração com Redis via Testcontainers</li>
 *   <li>Disponibilidade de beans essenciais no contexto Spring</li>
 *   <li>Funcionamento básico dos serviços de infraestrutura</li>
 * </ul>
 * 
 * <p>Serve como verificação inicial para garantir que a configuração básica
 * do ambiente de testes está correta antes de executar testes mais complexos.
 */
@Testcontainers
@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=" // Limpa exclusões anteriores
})
@ActiveProfiles("test")
public class EnvironmentSanityTest {

    @Container
    static GenericContainer<?> containerRedis = 
        new GenericContainer<>(DockerImageName.parse("redis:7.2.5"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configurarPropriedadesRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", containerRedis::getHost);
        registry.add("spring.data.redis.port", containerRedis::getFirstMappedPort);
    }

    @Autowired
    private ApplicationContext contextoAplicacao;

    /**
     * Verifica a disponibilidade e funcionalidade da conexão com Redis.
     * 
     * <p>Este teste garante que:
     * <ol>
     *   <li>O bean RedisConnectionFactory está disponível no contexto Spring</li>
     *   <li>É possível estabelecer conexão com o Redis em container</li>
     *   <li>Operações básicas de conexão/fechamento funcionam corretamente</li>
     * </ol>
     */
    @Test
    void deveValidarConexaoComRedis() {
        // Verifica presença do bean
        assertTrue(contextoAplicacao.containsBean("redisConnectionFactory"),
            "O bean 'redisConnectionFactory' deve estar disponível no contexto Spring");
        
        // Testa funcionalidade da conexão
        RedisConnectionFactory fabricaConexao = contextoAplicacao.getBean(RedisConnectionFactory.class);
        assertDoesNotThrow(() -> {
            try (var conexao = fabricaConexao.getConnection()) {
                assertNotNull(conexao, "A conexão com Redis não deve ser nula");
            }
        }, "Falha ao estabelecer conexão com Redis");
    }

    /**
     * Verifica o estado do container Redis.
     * 
     * <p>Garante que o container está em execução e com as portas expostas corretamente.
     */
    @Test
    void deveVerificarEstadoDoContainerRedis() {
        assertTrue(containerRedis.isRunning(), "O container Redis deve estar em execução");
        assertTrue(containerRedis.getFirstMappedPort() > 0, 
            "A porta do Redis deve estar mapeada corretamente");
    }

    /**
     * Verifica a configuração básica do contexto Spring.
     * 
     * <p>Garante que o contexto da aplicação foi carregado corretamente.
     */
    @Test
    void deveCarregarContextoSpringCorretamente() {
        assertNotNull(contextoAplicacao, "O contexto da aplicação deve ser carregado");
        assertTrue(contextoAplicacao.getBeanDefinitionCount() > 0,
            "O contexto deve conter beans definidos");
    }
}
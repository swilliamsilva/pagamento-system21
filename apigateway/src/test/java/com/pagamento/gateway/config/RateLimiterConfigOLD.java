package com.pagamento.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * 
 * 
 * <p>Define os beans necessários para implementação do rate limiting no Gateway:
 * <ul>
 *   <li>Configura o algoritmo de limitação com parâmetros de capacidade e recarga</li>
 *   <li>Define a estratégia de resolução de chaves para identificação de clientes</li>
 * </ul>
 * 
 * <p>Os parâmetros configurados:
 * <ul>
 *   <li>Taxa de recarga: 10 tokens por segundo</li>
 *   <li>Capacidade máxima: 20 tokens</li>
 *   <li>Tokens solicitados por operação: 1</li>
 * </ul>
 */
@Configuration
public class RateLimiterConfigOLD {

    /**
     * Configura o algoritmo de limitação de requisições usando Redis.
     * 
     * <p>Parâmetros:
     * <ul>
     *   <li>Taxa de recarga (replenishRate): 10 tokens por segundo</li>
     *   <li>Capacidade máxima (burstCapacity): 20 tokens</li>
     *   <li>Tokens por requisição (requestedTokens): 1</li>
     * </ul>
     * 
     * @return Instância configurada do RedisRateLimiter
     */
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 20, 1);
    }

    /**
     * Define a estratégia para identificação de clientes para limitação.
     * 
     * <p>Prioridades de identificação:
     * <ol>
     *   <li>Usa o cabeçalho "X-API-Key" quando disponível</li>
     *   <li>Utiliza "default" como chave para clientes não identificados</li>
     * </ol>
     * 
     * @return Resolvedor de chaves para identificação de clientes
     */
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> {
            String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-Key");
            return Mono.just(apiKey != null ? apiKey : "default");
        };
    }
}
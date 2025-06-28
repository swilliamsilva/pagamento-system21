package com.pagamento.common.config.db;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.InetSocketAddress;

/**
 * Configuração básica para Cassandra (usado no card-service).
 */
@Configuration
@Profile("cassandra")
public class CassandraConfig {
    @Bean
    public CqlSession cassandraSession() {
        return CqlSession.builder()
            .addContactPoint(new InetSocketAddress("localhost", 9042))
            .withLocalDatacenter("datacenter1")
            .build();
    }
}
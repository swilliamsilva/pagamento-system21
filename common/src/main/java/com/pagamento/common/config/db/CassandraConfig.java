package com.pagamento.common.config.db;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

/**
 * Configuração básica para Cassandra (usado no card-service).
 */
@Configuration
public class CassandraConfig {

    @Bean
    public CqlSession cassandraSession() {
        return CqlSession.builder()
            .addContactPoint(new InetSocketAddress("localhost", 9042)) // ajustar conforme ambiente
            .withLocalDatacenter("datacenter1")
            .build();
    }
}

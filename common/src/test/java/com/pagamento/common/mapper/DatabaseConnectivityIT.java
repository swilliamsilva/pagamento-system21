package com.pagamento.common.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@Testcontainers
class DatabaseConnectivityIT {
    
    @Container 
    static CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:4.0");
    
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cassandra.contact-points", cassandra::getContactPoint);
        registry.add("spring.cassandra.local-datacenter", cassandra::getLocalDatacenter);
    }
    
    @Autowired
    private CassandraTemplate cassandraTemplate;
    
    @Test
    void shouldConnectToCassandra() {
        assertDoesNotThrow(() -> 
            cassandraTemplate.getCqlOperations().execute("SELECT release_version FROM system.local"));
    }
}


package com.pagamento.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class DatabaseConnectivityIT {
    
    @Container
    private static final CassandraContainer<?> cassandra = 
            new CassandraContainer<>("cassandra:3.11.2")
            .withExposedPorts(9042);
    
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.cassandra.contact-points", 
            () -> cassandra.getHost() + ":" + cassandra.getMappedPort(9042));
        registry.add("spring.data.cassandra.local-datacenter", 
            () -> "datacenter1");
        registry.add("spring.data.cassandra.keyspace-name", 
            () -> "test_keyspace");
    }
    
    @Autowired
    private CassandraTemplate cassandraTemplate;
    
    @Test
    public void shouldConnectToCassandra() {
        assertDoesNotThrow(() -> 
            cassandraTemplate.getCqlOperations().execute(
                "SELECT release_version FROM system.local"
            )
        );
    }
}
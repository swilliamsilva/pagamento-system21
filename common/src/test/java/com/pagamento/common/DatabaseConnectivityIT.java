package com.pagamento.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import net.bytebuddy.utility.dispatcher.JavaDispatcher.Container;


@SpringBootTest
@Testcontainers
public class DatabaseConnectivityIT {
    
    @Container
    private static final CassandraContainer<?> cassandra = 
        new CassandraContainer<>("cassandra:4.0")
            .withExposedPorts(9042);
    
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.cassandra.contact-points", 
            () -> cassandra.getHost() + ":" + cassandra.getMappedPort(9042));
        registry.add("spring.data.cassandra.local-datacenter", 
            () -> "datacenter1");
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
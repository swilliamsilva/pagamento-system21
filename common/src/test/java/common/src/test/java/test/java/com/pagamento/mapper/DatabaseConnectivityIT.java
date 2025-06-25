
package common.src.test.java.test.java.com.pagamento.mapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import net.bytebuddy.utility.dispatcher.JavaDispatcher.Container;

// DatabaseConnectivityIT.java
@SpringBootTest
@Testcontainers
class DatabaseConnectivityIT {
    
    @Container static CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:4.0");
    
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

	private Object getContactPoint() {
		return null;
	}
}
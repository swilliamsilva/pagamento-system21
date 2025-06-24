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
}
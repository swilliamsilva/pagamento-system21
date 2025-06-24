// ResilienceConfigTest.java
class ResilienceConfigTest {
    
    @Test
    void shouldCreateCircuitBreakerRegistry() {
        ResilienceConfig config = new ResilienceConfig();
        CircuitBreakerRegistry registry = config.circuitBreakerRegistry();
        assertNotNull(registry);
    }
    
    @Test
    void shouldCreateRetryRegistry() {
        ResilienceConfig config = new ResilienceConfig();
        RetryRegistry registry = config.retryRegistry();
        assertNotNull(registry);
    }
}
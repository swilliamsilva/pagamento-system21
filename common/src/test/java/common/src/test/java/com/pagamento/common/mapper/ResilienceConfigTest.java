package common.src.test.java.com.pagamento.common.mapper;


import org.junit.jupiter.api.Test;

import com.pagamento.common.resilience.ResilienceConfig;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

// ResilienceConfigTest.java
class ResilienceConfigTest {
    
    @Test
    void shouldCreateCircuitBreakerRegistry() {
        ResilienceConfig config = new ResilienceConfig();
        CircuitBreakerRegistry registry = config.circuitBreakerRegistry();
        assertNotNull(registry);
    }
    
    private void assertNotNull(CircuitBreakerRegistry registry) {
		// TODO Auto-generated method stub
		
	}

	@Test
    void shouldCreateRetryRegistry() {
        ResilienceConfig config = new ResilienceConfig();
        RetryRegistry registry = config.retryRegistry();
        assertNotNull(registry);
    }
}
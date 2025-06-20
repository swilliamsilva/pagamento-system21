package com.pagamento.common.observability;



import io.micrometer.core.instrument.MeterRegistry;

/**
 * 
 * the import io.micrometer.core cannot be resolved
 * 
 * 
 * **/

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Configuração para publicar métricas customizadas nos serviços.
 */
@Configuration
public class MetricsConfig {

    private final MeterRegistry meterRegistry;
    /**
     * 
     * MeterRegistry cannot be resolved to a type
     * 
     * **/
    
    

    public MetricsConfig(MeterRegistry meterRegistry) {
    	/**
    	 * 
    	 * MeterRegistry cannot be resolved to a type
    	 * 
    	 * **/
    	
    	
    	
        this.meterRegistry = meterRegistry;
        /**
         * 
         * 
         * MeterRegistry cannot be resolved to a type
         * 
         * **/
        
        
    }

    @PostConstruct
    public void initMetrics() {
        meterRegistry.counter("pagamento.requests.total").increment(0);
        /**
         * 
         * 
         * MeterRegistry cannot be resolved to a type
         * 
         * 
         * **/
        
    }
}

// src/main/java/com/pagamento/boleto/infrastructure/adapters/repository/BoletoRepositoryAdapter.java
// Para persistência real, você poderá usar Mongo ou PostgreSQL e adaptar para um JpaRepository ou MongoRepository real.

package com.pagamento.boleto.infrastructure.adapters.repository;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.ports.BoletoRepositoryPort;
import org.springframework.stereotype.Component;
/**
 * 
 * 
 * The import org.springframework cannot be resolved
 * 
 * 
 * **/


import java.util.*;

@Component
/**
 * 
 * 
 * Component cannot be resolved to a type
 * 
 * **/

public class BoletoRepositoryAdapter implements BoletoRepositoryPort {
/**
 * 
 * 
 * The type BoletoRepositoryAdapter must implement the inherited
 *  abstract method BoletoRepositoryPort.salvar(Boleto)
 * 
 * 
 * **/
    private final Map<String, Boleto> fakeDatabase = new HashMap<>();
    
    /**
     * 
     * 
     * Multiple markers at this line
	- Boleto cannot be resolved to a type
	- Cannot infer type arguments for HashMap<>
     * 
     * **/

    @Override
    public Optional<Boleto> findById(String id) {
    	
    	/**
    	 * 
    	 * 
    	 * Multiple markers at this line
	- implements com.pagamento.boleto.domain.ports.BoletoRepositoryPort.findById
	- Boleto cannot be resolved to a type
    	 * 
    	 * **/
    	
    	
        return Optional.ofNullable(fakeDatabase.get(id));
        
        /**
         * 
         * Boleto cannot be resolved to a type
         * 
         * 
         * **/
        
    }

    @Override
    public Boleto save(Boleto boleto) {
        fakeDatabase.put(boleto.getId(), boleto);
        return boleto;
    }

    @Override
    public List<Boleto> findAll() {
        return new ArrayList<>(fakeDatabase.values());
    }

    @Override
    public void deleteById(String id) {
        fakeDatabase.remove(id);
    }
}

package com.pagamento.boleto.domain.ports;

import java.util.List;
import java.util.Optional;

import com.pagamento.boleto.domain.model.Boleto;

/**
 * 
 * The import com.pagamento.boleto.domain.model cannot be resolved
 * 
 * **/


public interface BoletoRepositoryPort {
    void salvar(Boleto boleto);

	Optional<Boleto> findById(String id);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * **/
	

	Boleto save(Boleto boleto);

	List<Boleto> findAll();
	
	
	/**
	 * 
	 * Boleto cannot be resolved to a type
	 * 
	 * 
	 * 
	 * **/

	void deleteById(String id);
}

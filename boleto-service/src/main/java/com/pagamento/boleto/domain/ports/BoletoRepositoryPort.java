package com.pagamento.boleto.domain.ports;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BoletoRepositoryPort {
    Boleto salvar(Boleto boleto);
    Optional<Boleto> buscarPorId(String id);
    Boleto atualizar(Boleto boleto);
    List<Boleto> buscarTodos();
    void deletarPorId(String id);
    List<Boleto> buscarPorStatus(BoletoStatus status);
   
    
    
    List<Boleto> buscarVencidos(LocalDate dataAtual);
	
}
package com.pagamento.boleto.domain.ports;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BoletoRepositoryPort {
    Boleto save(Boleto boleto);
    Optional<Boleto> findById(String id);
    List<Boleto> findAll();
    void deleteById(String id);
    List<Boleto> findByStatus(BoletoStatus status);
    List<Boleto> findByDataVencimentoBetween(LocalDate start, LocalDate end);
    List<Boleto> findByDocumentoPagador(String documento);
    List<Boleto> buscarVencidos(LocalDate dataAtual);
}
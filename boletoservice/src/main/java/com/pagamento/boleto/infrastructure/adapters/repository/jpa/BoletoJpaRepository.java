// Repository Interface: BoletoJpaRepository.java
package com.pagamento.boleto.infrastructure.adapters.repository.jpa;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BoletoJpaRepository extends JpaRepository<Boleto, UUID> {
    List<Boleto> findByStatus(BoletoStatus status);
    List<Boleto> findByDataVencimentoBeforeAndStatusNot(LocalDate dataAtual, BoletoStatus pago);
    List<Boleto> findByDataVencimentoBetween(LocalDate inicio, LocalDate fim);
    List<Boleto> findByDocumento(String documento);
}
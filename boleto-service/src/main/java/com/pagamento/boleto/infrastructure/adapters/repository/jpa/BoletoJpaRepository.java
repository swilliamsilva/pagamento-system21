package com.pagamento.boleto.infrastructure.adapters.repository.jpa;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoletoJpaRepository extends JpaRepository<Boleto, String> {

	List<Boleto> findByStatus(BoletoStatus status);

	List<Boleto> findByDataVencimentoBeforeAndStatusNot(LocalDate dataAtual, BoletoStatus pago);
}
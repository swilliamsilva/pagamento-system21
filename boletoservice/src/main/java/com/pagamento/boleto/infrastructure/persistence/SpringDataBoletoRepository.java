package com.pagamento.boleto.infrastructure.persistence;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SpringDataBoletoRepository extends JpaRepository<Boleto, String> {
    List<Boleto> findByStatus(BoletoStatus status);
    List<Boleto> findByDataVencimentoBefore(LocalDate dataLimite);
    List<Boleto> findByDocumento(String documento);
    List<Boleto> findByDataVencimentoBetween(LocalDate inicio, LocalDate fim);
}
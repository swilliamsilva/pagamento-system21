package com.pagamento.boleto.infrastructure.persistence;

import com.pagamento.boleto.domain.model.BoletoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataBoletoRepository extends JpaRepository<BoletoEntity, UUID> {
    
    List<BoletoEntity> findByStatus(BoletoStatus status);
    
    List<BoletoEntity> findByDataVencimentoBeforeAndStatusNotIn(
        LocalDate dataLimite, 
        List<BoletoStatus> statuses
    );
    
    List<BoletoEntity> findByDocumento(String documento);
    
    List<BoletoEntity> findByDataVencimentoBetween(
        LocalDate inicio, 
        LocalDate fim
    );
}
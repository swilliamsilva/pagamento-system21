package com.pagamento.card.infrastructure.persistence;

import com.pagamento.card.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}

// Serviço de Auditoria

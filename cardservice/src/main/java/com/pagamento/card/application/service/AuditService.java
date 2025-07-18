package com.pagamento.card.application.service;

import com.pagamento.card.domain.model.Transaction;
import com.pagamento.card.infrastructure.persistence.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final TransactionRepository repository;

    public AuditService(TransactionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void saveTransaction(CardResponseDTO response) {
        Transaction transaction = new Transaction(
            response.getTransactionId(),
            response.getBandeira(),
            response.getStatus().name(),
            response.getValor(),
            response.getCodigoAutorizacao(),
            response.getMensagem()
        );
        
        repository.save(transaction);
    }
}

// Atualização do PaymentService

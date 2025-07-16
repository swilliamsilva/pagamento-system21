package com.pagamento.pix.application.service;

import com.pagamento.common.payment.PaymentOrchestratorPort;
import com.pagamento.common.payment.TransactionResponse;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.domain.ports.PixRepositoryPort;
import com.pagamento.pix.domain.ports.PixServicePort;
import org.springframework.stereotype.Service;

@Service
public class PixServiceImpl implements PixServicePort {

    private final PaymentOrchestratorPort paymentOrchestrator;
    private final PixRepositoryPort pixRepository;

    public PixServiceImpl(
        PaymentOrchestratorPort paymentOrchestrator,
        PixRepositoryPort pixRepository
    ) {
        this.paymentOrchestrator = paymentOrchestrator;
        this.pixRepository = pixRepository;
    }

    @Override
    public Pix processarPix(Pix pix) {
        if (!pix.isValid()) {
            throw new PixValidationException("Dados do Pix inválidos");
        }
        
        // Orquestração via PaymentService
        TransactionResponse response = paymentOrchestrator.orchestrate(pix);
        pix.setStatus(response.getStatus().name());
        
        // Persistência
        return pixRepository.salvar(pix);
    }
}
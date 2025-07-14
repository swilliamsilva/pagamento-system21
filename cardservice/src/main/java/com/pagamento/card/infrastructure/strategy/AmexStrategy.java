package com.pagamento.card.infrastructure.strategy;

import com.pagamento.card.application.dto.CardRequestDTO;
import com.pagamento.card.application.dto.CardResponseDTO;
import com.pagamento.card.domain.enums.PaymentStatus;
import com.pagamento.card.domain.strategy.BandeiraStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("amex") // Nome do bean alterado para "amex"
public class AmexStrategy implements BandeiraStrategy {

    @Override
    public CardResponseDTO processarPagamento(CardRequestDTO request) {
        // Simulação de lógica específica AMEX
        boolean isAmex = request.getNumeroCartao().matches("^3[47].*");
        boolean validCVV = request.getCvv().length() == 4;
        
        if (!isAmex) {
            return new CardResponseDTO(
                "AMEX-ERR-" + System.currentTimeMillis(),
                "AMEX",
                PaymentStatus.DECLINED,
                request.getValor(),
                null,
                "Número de cartão não compatível com bandeira AMEX"
            );
        }
        
        if (!validCVV) {
            return new CardResponseDTO(
                "AMEX-ERR-" + System.currentTimeMillis(),
                "AMEX",
                PaymentStatus.FRAUD_SUSPECTED,
                request.getValor(),
                null,
                "CVV inválido para cartão AMEX"
            );
        }
        
        return new CardResponseDTO(
            "AMEX-TXN-" + System.currentTimeMillis(),
            "AMEX",
            PaymentStatus.APPROVED,
            request.getValor(),
            "AMEX" + System.currentTimeMillis(),
            "Transação aprovada"
        );
    }
}
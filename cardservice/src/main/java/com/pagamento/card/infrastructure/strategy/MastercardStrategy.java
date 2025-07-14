package com.pagamento.card.infrastructure.strategy;

import com.pagamento.card.application.dto.CardRequestDTO;
import com.pagamento.card.application.dto.CardResponseDTO;
import com.pagamento.card.domain.enums.PaymentStatus;
import com.pagamento.card.domain.strategy.BandeiraStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("mastercard") // Nome do bean alterado para "mastercard"
public class MastercardStrategy implements BandeiraStrategy {

    @Override
    public CardResponseDTO processarPagamento(CardRequestDTO request) {
        try {
            // Simulação de regras específicas Mastercard
            boolean isCorporate = request.getNumeroCartao().startsWith("54");
            boolean requires3DS = request.getValor().compareTo(BigDecimal.valueOf(1000)) > 0;
            
            String authPrefix = isCorporate ? "MC-CORP" : "MC-STD";
            String authCode = authPrefix + "-" + System.currentTimeMillis();
            
            return new CardResponseDTO(
                "MC-TXN-" + System.currentTimeMillis(),
                "MASTERCARD",
                PaymentStatus.APPROVED,
                request.getValor(),
                authCode,
                requires3DS ? "Requer autenticação 3DS" : "Transação aprovada"
            );
        } catch (Exception e) {
            return new CardResponseDTO(
                "MC-ERR-" + System.currentTimeMillis(),
                "MASTERCARD",
                PaymentStatus.PROCESSING_ERROR,
                request.getValor(),
                null,
                "Erro no processamento Mastercard: " + e.getMessage()
            );
        }
    }
}
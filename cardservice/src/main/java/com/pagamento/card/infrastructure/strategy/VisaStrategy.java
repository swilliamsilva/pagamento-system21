package com.pagamento.card.infrastructure.strategy;

import com.pagamento.card.application.dto.CardRequestDTO;
import com.pagamento.card.application.dto.CardResponseDTO;
import com.pagamento.card.domain.enums.PaymentStatus;
import com.pagamento.card.domain.strategy.BandeiraStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

@Component("visa")  // Fixed bean name to follow lowercase-first convention
public class VisaStrategy implements BandeiraStrategy {

    private static final Random RANDOM = new Random();
    
    @Override
    public CardResponseDTO processarPagamento(CardRequestDTO request) {
        try {
            // Removed unused isInternational variable
            boolean highRisk = request.getValor().compareTo(BigDecimal.valueOf(5000)) > 0;
            
            // 10% de chance de declínio para valores altos
            if (highRisk && RANDOM.nextDouble() < 0.1) {
                return new CardResponseDTO(
                    "VISA-TXN-" + System.currentTimeMillis(),
                    "VISA",
                    PaymentStatus.DECLINED,
                    request.getValor(),
                    null,
                    "Transação recusada por política de risco"
                );
            }
            
            String authCode = "VISA" + (100000 + RANDOM.nextInt(900000));
            
            return new CardResponseDTO(
                "VISA-TXN-" + System.currentTimeMillis(),
                "VISA",
                PaymentStatus.APPROVED,
                request.getValor(),
                authCode,
                "Transação aprovada"
            );
        } catch (Exception e) {
            return new CardResponseDTO(
                "VISA-ERR-" + System.currentTimeMillis(),
                "VISA",
                PaymentStatus.PROCESSING_ERROR,
                request.getValor(),
                null,
                "Erro no processamento Visa: " + e.getMessage()
            );
        }
    }
}
package com.pagamento.card.infrastructure.strategy;

import org.springframework.stereotype.Component;

import com.pagamento.card.application.dto.CardRequestDTO;
import com.pagamento.card.application.dto.CardResponseDTO;
import com.pagamento.card.domain.enums.PaymentStatus;
import com.pagamento.card.domain.strategy.BandeiraStrategy;

@Component("elo") // Nome do bean alterado para "elo"
public class EloStrategy implements BandeiraStrategy {

    @Override
    public CardResponseDTO processarPagamento(CardRequestDTO request) {
        // Simulação de lógica específica ELO
        boolean isElo = request.getNumeroCartao().matches("^(431274|451416|5067|5090|627780).*");
        
        if (!isElo) {
            return new CardResponseDTO(
                "ELO-ERR-" + System.currentTimeMillis(),
                "ELO",
                PaymentStatus.DECLINED,
                request.getValor(),
                null,
                "Número de cartão não compatível com bandeira ELO"
            );
        }
        
        return new CardResponseDTO(
            "ELO-TXN-" + System.currentTimeMillis(),
            "ELO",
            PaymentStatus.APPROVED,
            request.getValor(),
            "ELO" + System.currentTimeMillis(),
            "Transação aprovada"
        );
    }
}
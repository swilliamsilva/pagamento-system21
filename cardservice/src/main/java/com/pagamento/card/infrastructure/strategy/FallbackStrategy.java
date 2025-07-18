package com.pagamento.card.infrastructure.strategy;

import com.pagamento.card.application.dto.CardRequestDTO;
import com.pagamento.card.application.dto.CardResponseDTO;
import com.pagamento.card.domain.enums.PaymentStatus;
import com.pagamento.card.domain.strategy.BandeiraStrategy;
import org.springframework.stereotype.Component;

@Component("fallback") // Nome do bean alterado para "fallback"
public class FallbackStrategy implements BandeiraStrategy {

    @Override
    public CardResponseDTO processarPagamento(CardRequestDTO request) {
        return new CardResponseDTO(
            "UNK-TXN-" + System.currentTimeMillis(),
            request.getBandeira(),
            PaymentStatus.UNSUPPORTED_CARD,
            request.getValor(),
            null,
            "Bandeira não suportada: " + request.getBandeira()
        );
    }
}
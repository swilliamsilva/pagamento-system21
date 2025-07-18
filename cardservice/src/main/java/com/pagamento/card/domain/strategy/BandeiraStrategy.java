package com.pagamento.card.domain.strategy;

import com.pagamento.card.application.dto.CardRequestDTO;
import com.pagamento.card.application.dto.CardResponseDTO;

public interface BandeiraStrategy {
    CardResponseDTO processarPagamento(CardRequestDTO request);
}
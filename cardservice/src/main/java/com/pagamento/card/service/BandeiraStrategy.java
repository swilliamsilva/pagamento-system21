package com.pagamento.card.service;

import com.pagamento.card.application.dto.CardRequestDTO;
import com.pagamento.card.application.dto.CardResponseDTO;

public interface BandeiraStrategy {
    CardResponseDTO processarPagamento(CardRequestDTO request);
}
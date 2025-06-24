/* ========================================================
# Classe: CardMapper
# Módulo: card-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Conversão entre entidade e DTO de Cartão.
# ======================================================== */

package com.pagamento.card.application.mapper;

import com.pagamento.card.model.Card;
import com.pagamento.card.application.dto.CardRequestDTO;
import com.pagamento.card.application.dto.CardResponseDTO;

public class CardMapper {

    public static Card toEntity(CardRequestDTO dto) {
        Card card = new Card();
        // Preencher com os campos necessários
        return card;
    }

    public static CardResponseDTO toResponse(Card entity) {
        CardResponseDTO dto = new CardResponseDTO();
        // Preencher com os campos necessários
        return dto;
    }
}

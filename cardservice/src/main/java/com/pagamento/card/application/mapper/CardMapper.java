/* ========================================================
# Classe: CardMapper
# Módulo: card-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Conversão entre entidades de domínio e DTOs
# ======================================================== */

package com.pagamento.card.application.mapper;

import com.pagamento.card.domain.model.Transaction;
import com.pagamento.card.application.dto.CardRequestDTO;
import com.pagamento.card.application.dto.CardResponseDTO;
import com.pagamento.card.domain.enums.PaymentStatus;

public final class CardMapper {

    // Construtor privado para evitar instanciação
    private CardMapper() {
        throw new AssertionError("Classe utilitária não deve ser instanciada");
    }

    /**
     * Converte CardRequestDTO para entidade Transaction
     */
    public static Transaction toTransactionEntity(CardRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Transaction.builder()
                .cardNumber(dto.getNumeroCartao())
                .brand(dto.getBandeira())
                .expiryDate(dto.getDataValidade())
                .cvv(dto.getCvv())
                .amount(dto.getValor())
                .cardHolder(dto.getNomeTitular())
                .installments(dto.getParcelas())
                .build();
    }

    /**
     * Converte entidade Transaction para CardResponseDTO
     */
    public static CardResponseDTO toResponseDTO(Transaction entity) {
        if (entity == null) {
            return null;
        }

        return new CardResponseDTO(
                entity.getId(),
                entity.getBrand(),
                PaymentStatus.valueOf(entity.getStatus()),
                entity.getAmount(),
                entity.getAuthorizationCode(),
                entity.getMessage()
        );
    }

    /**
     * Atualiza entidade Transaction com dados do DTO de resposta
     */
    public static void updateEntityFromResponse(Transaction entity, CardResponseDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setStatus(dto.getStatus().name());
        entity.setAuthorizationCode(dto.getCodigoAutorizacao());
        entity.setMessage(dto.getMensagem());
    }
}
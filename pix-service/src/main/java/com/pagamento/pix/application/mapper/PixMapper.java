/* ========================================================
# Classe: PixMapper
# Módulo: pix-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Conversão entre Pix e seus DTOs.
# ======================================================== */

package com.pagamento.pix.application.mapper;

import com.pagamento.pix.application.dto.PixRequestDTO;
import com.pagamento.pix.application.dto.PixResponseDTO;
import com.pagamento.pix.model.Pix;

import java.time.LocalDateTime;

public class PixMapper {

    /**
     * Converte um PixRequestDTO para a entidade Pix.
     *
     * @param dto dados da requisição
     * @return entidade Pix pronta para persistência
     */
    public static Pix toEntity(PixRequestDTO dto) {
        Pix pix = new Pix();
        pix.setChaveDestino(dto.getChaveDestino());
        pix.setTipo(dto.getTipo());
        pix.setValor(dto.getValor());
        pix.setDataTransacao(LocalDateTime.now()); // Define a data atual da transação
        return pix;
    }

    /**
     * Converte uma entidade Pix para PixResponseDTO.
     *
     * @param entity entidade persistida
     * @return DTO de resposta para API
     */
    public static PixResponseDTO toResponse(Pix entity) {
        PixResponseDTO dto = new PixResponseDTO();
        dto.setId(entity.getId());
        dto.setChaveDestino(entity.getChaveDestino());
        dto.setTipo(entity.getTipo());
        dto.setValor(entity.getValor());
        dto.setDataTransacao(entity.getDataTransacao());
        return dto;
    }
}

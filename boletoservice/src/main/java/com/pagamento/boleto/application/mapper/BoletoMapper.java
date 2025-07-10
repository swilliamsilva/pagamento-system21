package com.pagamento.boleto.application.mapper;

import com.pagamento.boleto.application.dto.BoletoResponseDTO;
import com.pagamento.boleto.domain.model.Boleto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BoletoMapper {

    // Construtor privado para evitar instanciação
    private BoletoMapper() {
        throw new UnsupportedOperationException("Esta é uma classe utilitária e não pode ser instanciada");
    }

    public static BoletoResponseDTO toDTO(Boleto boleto) {
        return new BoletoResponseDTO(
            boleto.getId(),
            boleto.getPagador(),
            boleto.getBeneficiario(),
            boleto.getValor(),
            boleto.getDataVencimento(),
            boleto.getDataEmissao(),
            boleto.getDocumento(),
            boleto.getInstrucoes(),
            boleto.getLocalPagamento(),
            boleto.getStatus().name(),
            boleto.getMotivoCancelamento(),
            boleto.getNumeroReemissoes(),
            boleto.getBoletoOriginalId()
        );
    }
}
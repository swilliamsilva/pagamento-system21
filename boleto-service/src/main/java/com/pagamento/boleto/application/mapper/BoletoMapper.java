package com.pagamento.boleto.application.mapper;

import com.pagamento.boleto.application.dto.BoletoResponseDTO;
import com.pagamento.boleto.domain.model.Boleto;

public class BoletoMapper {

    public static BoletoResponseDTO toDTO(Boleto boleto) {
        return new BoletoResponseDTO(
            boleto.getId(),
            boleto.getPagador(),
            boleto.getBeneficiario(),
            boleto.getValor(),
            boleto.getDataEmissao(),
            boleto.getDataVencimento(),
            boleto.getStatus().name(),
            boleto.getCodigoBarras(),
            boleto.getLinhaDigitavel(),
            boleto.getQrCode(),
            boleto.getDocumento(),
            boleto.getInstrucoes(),
            boleto.getLocalPagamento(),
            boleto.getBoletoOriginalId(),
            boleto.getNumeroReemissoes()
        );
    }
}
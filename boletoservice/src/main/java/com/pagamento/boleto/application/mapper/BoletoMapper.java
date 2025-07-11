package com.pagamento.boleto.application.mapper;

import com.pagamento.boleto.application.dto.BoletoResponseDTO;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.infrastructure.persistence.BoletoEntity;

public class BoletoMapper {

    private BoletoMapper() {
        throw new UnsupportedOperationException("Esta é uma classe utilitária e não pode ser instanciada");
    }

    public static BoletoResponseDTO toDTO(Boleto boleto) {
        return new BoletoResponseDTO(
            boleto.getId().toString(),
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

    public static BoletoEntity toEntity(Boleto boleto) {
        BoletoEntity entity = new BoletoEntity();
        entity.setId(boleto.getId());
        entity.setPagador(boleto.getPagador());
        entity.setBeneficiario(boleto.getBeneficiario());
        entity.setValor(boleto.getValor());
        entity.setDataEmissao(boleto.getDataEmissao());
        entity.setDataVencimento(boleto.getDataVencimento());
        entity.setStatus(boleto.getStatus());
        entity.setDocumento(boleto.getDocumento());
        entity.setInstrucoes(boleto.getInstrucoes());
        entity.setLocalPagamento(boleto.getLocalPagamento());
        entity.setBoletoOriginalId(boleto.getBoletoOriginalId());
        entity.setNumeroReemissoes(boleto.getNumeroReemissoes());
        entity.setMotivoCancelamento(boleto.getMotivoCancelamento());
        return entity;
    }

    public static Boleto toDomain(BoletoEntity entity) {
        return Boleto.builder()
            .id(entity.getId())
            .pagador(entity.getPagador())
            .beneficiario(entity.getBeneficiario())
            .valor(entity.getValor())
            .dataEmissao(entity.getDataEmissao())
            .dataVencimento(entity.getDataVencimento())
            .status(entity.getStatus())
            .documento(entity.getDocumento())
            .instrucoes(entity.getInstrucoes())
            .localPagamento(entity.getLocalPagamento())
            .boletoOriginalId(entity.getBoletoOriginalId())
            .numeroReemissoes(entity.getNumeroReemissoes())
            .motivoCancelamento(entity.getMotivoCancelamento())
            .build();
    }
}
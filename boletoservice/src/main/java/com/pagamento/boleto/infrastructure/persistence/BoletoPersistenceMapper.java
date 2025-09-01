package com.pagamento.boleto.infrastructure.persistence;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatusHistorico;
import com.pagamento.boleto.domain.model.DadosTecnicosBoleto;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BoletoPersistenceMapper {

    public BoletoEntity toEntity(Boleto boleto) {
        if (boleto == null) {
            return null;
        }
        return BoletoEntity.builder()
                .id(boleto.getId())
                .pagador(boleto.getPagador())
                .beneficiario(boleto.getBeneficiario())
                .valor(boleto.getValor())
                .dataEmissao(boleto.getDataEmissao())
                .dataVencimento(boleto.getDataVencimento())
                .dataPagamento(boleto.getDataPagamento().orElse(null))
                .status(boleto.getStatus())
                .historicoStatus(boleto.getHistoricoStatus())
                .documento(boleto.getDocumento())
                .instrucoes(boleto.getInstrucoes())
                .localPagamento(boleto.getLocalPagamento())
                .dadosTecnicos(boleto.getDadosTecnicos().orElse(null))
                .idExterno(boleto.getIdExterno().orElse(null))
                .boletoOriginalId(boleto.getBoletoOriginalId().map(String::valueOf).orElse(null))
                .numeroReemissoes(boleto.getNumeroReemissoes())
                .motivoCancelamento(boleto.getMotivoCancelamento().orElse(null))
                .dataUltimaAtualizacao(boleto.getDataUltimaAtualizacao())
                .build();
    }

    public Boleto toDomain(BoletoEntity entity) {
        if (entity == null) {
            return null;
        }
        Boleto boleto = new Boleto(
                entity.getId(),
                entity.getPagador(),
                entity.getBeneficiario(),
                entity.getValor(),
                entity.getDataEmissao(),
                entity.getDataVencimento(),
                Optional.ofNullable(entity.getDataPagamento()),
                entity.getStatus(),
                entity.getHistoricoStatus() != null ? entity.getHistoricoStatus() : new java.util.ArrayList<>(),
                entity.getDocumento(),
                entity.getInstrucoes(),
                entity.getLocalPagamento(),
                Optional.ofNullable(entity.getDadosTecnicos()),
                Optional.ofNullable(entity.getIdExterno()),
                Optional.ofNullable(entity.getBoletoOriginalId()).map(java.util.UUID::fromString),
                entity.getNumeroReemissoes(),
                Optional.ofNullable(entity.getMotivoCancelamento()),
                entity.getDataUltimaAtualizacao()
        );
        return boleto;
    }
}



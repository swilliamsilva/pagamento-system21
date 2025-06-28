package com.pagamento.boleto.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.service.BoletoCalculos;

public class BoletoFactory {
    
    private final BoletoCalculos calculos;
    
    public BoletoFactory(BoletoCalculos calculos) {
        this.calculos = calculos;
    }
    
    public Boleto criarBoleto(BoletoRequestDTO dto) {
        // Validação de campos obrigatórios
        if (dto.valor() == null || dto.valor() <= 0) {
            throw new IllegalArgumentException("Valor do boleto inválido");
        }
        if (dto.dataVencimento() == null) {
            throw new IllegalArgumentException("Data de vencimento obrigatória");
        }
        if (dto.pagador() == null || dto.pagador().isBlank()) {
            throw new IllegalArgumentException("Pagador é obrigatório");
        }
        if (dto.beneficiario() == null || dto.beneficiario().isBlank()) {
            throw new IllegalArgumentException("Beneficiário é obrigatório");
        }

        // Construir o boleto usando o padrão Builder
        Boleto boleto = Boleto.builder()
            .pagador(dto.pagador())
            .beneficiario(dto.beneficiario())
            .valor(BigDecimal.valueOf(dto.valor()))
            .dataVencimento(dto.dataVencimento())
            .dataEmissao(dto.dataEmissao() != null ? dto.dataEmissao() : LocalDate.now())
            .documento(dto.documento() != null ? dto.documento() : "")
            .instrucoes(dto.instrucoes() != null ? dto.instrucoes() : "")
            .localPagamento(dto.localPagamento() != null ? dto.localPagamento() : "Pagável em qualquer banco")
            .status(BoletoStatus.EMITIDO)
            .build();

        // Gerar dados técnicos DEPOIS da construção básica
        boleto.setDadosTecnicos(calculos.gerarDadosTecnicos(boleto));
        
        return boleto;
    }
    
    public Boleto criarReemissao(Boleto original, int diasAdicionaisVencimento) {
        // Construir a reemissão usando o padrão Builder
        Boleto reemissao = Boleto.builder()
            .pagador(original.getPagador())
            .beneficiario(original.getBeneficiario())
            .valor(original.getValor())
            .documento(original.getDocumento())
            .instrucoes(original.getInstrucoes())
            .localPagamento(original.getLocalPagamento())
            .dataEmissao(LocalDate.now())
            .dataVencimento(original.getDataVencimento().plusDays(diasAdicionaisVencimento))
            .boletoOriginalId(original.getId())
            .status(BoletoStatus.REEMITIDO)
            .build();

        // Gerar dados técnicos DEPOIS da construção básica
        reemissao.setDadosTecnicos(calculos.gerarDadosTecnicos(reemissao));
        
        return reemissao;
    }
}
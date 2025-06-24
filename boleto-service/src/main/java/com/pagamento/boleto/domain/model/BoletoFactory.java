package com.pagamento.boleto.domain.model;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.service.BoletoCalculos;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class BoletoFactory {
    
    private final BoletoCalculos calculos;
    
    public BoletoFactory(BoletoCalculos calculos) {
        this.calculos = calculos;
    }
    
    public Boleto criarBoleto(BoletoRequestDTO dto) {
        Boleto boleto = new Boleto();
        boleto.setId(UUID.randomUUID().toString());
        boleto.setPagador(dto.pagador());
        boleto.setBeneficiario(dto.beneficiario());
        boleto.setValor(dto.valor());
        /**
         * 
         * The method setValor(BigDecimal) in the type Boleto is not applicable for the arguments (Double)
         * **/
        boleto.setDataVencimento(dto.dataVencimento());
        boleto.setDataEmissao(dto.dataEmissao() != null ? dto.dataEmissao() : LocalDate.now());
        boleto.setDocumento(dto.documento() != null ? dto.documento() : "");
        boleto.setInstrucoes(dto.instrucoes() != null ? dto.instrucoes() : "");
        boleto.setLocalPagamento(dto.localPagamento() != null ? dto.localPagamento() : "Pagável em qualquer banco");
        boleto.setStatus(BoletoStatus.EMITIDO);
        
        // Gerar dados técnicos
        boleto.setCodigoBarras(calculos.gerarCodigoBarras(boleto));
        boleto.setLinhaDigitavel(calculos.gerarLinhaDigitavel(boleto.getCodigoBarras()));
        boleto.setQrCode(calculos.gerarQRCode(boleto));
        boleto.setNossoNumero(calculos.gerarNossoNumero());
        
        return boleto;
    }
    
    public Boleto criarReemissao(Boleto original) {
        Boleto reemissao = new Boleto();
        reemissao.setId(UUID.randomUUID().toString());
        reemissao.setPagador(original.getPagador());
        reemissao.setBeneficiario(original.getBeneficiario());
        reemissao.setValor(original.getValor());
        reemissao.setDocumento(original.getDocumento());
        reemissao.setInstrucoes(original.getInstrucoes());
        reemissao.setLocalPagamento(original.getLocalPagamento());
        reemissao.setDataEmissao(LocalDate.now());
        reemissao.setDataVencimento(original.getDataVencimento().plusDays(30));
        reemissao.setBoletoOriginalId(original.getId());
        reemissao.setStatus(BoletoStatus.REEMITIDO);
        
        // Gerar novos dados técnicos
        reemissao.setCodigoBarras(calculos.gerarCodigoBarras(reemissao));
        reemissao.setLinhaDigitavel(calculos.gerarLinhaDigitavel(reemissao.getCodigoBarras()));
        reemissao.setQrCode(calculos.gerarQRCode(reemissao));
        reemissao.setNossoNumero(calculos.gerarNossoNumero());
        
        return reemissao;
    }
}
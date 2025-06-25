package com.pagamento.boleto.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class Boleto {
    private String id;
    private String pagador;
    private String beneficiario;
    private BigDecimal valor;
    private LocalDate dataEmissao;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private BoletoStatus status;
    private String documento;
    private String instrucoes;
    private String localPagamento;
    private String codigoBarras;
    private String linhaDigitavel;
    private String qrCode;
    private String nossoNumero;
    private String idExterno;
    private String boletoOriginalId;
    private int numeroReemissoes;
    private String motivoCancelamento;

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getPagador() { return pagador; }
    public void setPagador(String pagador) { this.pagador = pagador; }
    
    public String getBeneficiario() { return beneficiario; }
    public void setBeneficiario(String beneficiario) { this.beneficiario = beneficiario; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(@Positive(message = "Valor deve ser positivo") @NotNull(message = "Valor é obrigatório") BigDecimal double1) { this.valor = double1; }
    
    public LocalDate getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDate dataEmissao) { this.dataEmissao = dataEmissao; }
    
    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }
    
    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }
    
    public BoletoStatus getStatus() { return status; }
    public void setStatus(BoletoStatus status) { this.status = status; }
    
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    
    public String getInstrucoes() { return instrucoes; }
    public void setInstrucoes(String instrucoes) { this.instrucoes = instrucoes; }
    
    public String getLocalPagamento() { return localPagamento; }
    public void setLocalPagamento(String localPagamento) { this.localPagamento = localPagamento; }
    
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
    
    public String getLinhaDigitavel() { return linhaDigitavel; }
    public void setLinhaDigitavel(String linhaDigitavel) { this.linhaDigitavel = linhaDigitavel; }
    
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    
    public String getNossoNumero() { return nossoNumero; }
    public void setNossoNumero(String nossoNumero) { this.nossoNumero = nossoNumero; }
    
    public String getIdExterno() { return idExterno; }
    public void setIdExterno(String idExterno) { this.idExterno = idExterno; }
    
    public String getBoletoOriginalId() { return boletoOriginalId; }
    public void setBoletoOriginalId(String boletoOriginalId) { this.boletoOriginalId = boletoOriginalId; }
    
    public int getNumeroReemissoes() { return numeroReemissoes; }
    public void incrementarReemissoes() { this.numeroReemissoes++; }
    
    public String getMotivoCancelamento() { return motivoCancelamento; }
    public void setMotivoCancelamento(String motivoCancelamento) { this.motivoCancelamento = motivoCancelamento; }
    
    // Métodos de negócio
    public void marcarComoPago(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
        this.status = BoletoStatus.PAGO;
    }
    
    public void cancelar(String motivo) {
        this.status = BoletoStatus.CANCELADO;
        this.motivoCancelamento = motivo;
    }
    
    public boolean isVencido() {
        return LocalDate.now().isAfter(dataVencimento);
    }
}
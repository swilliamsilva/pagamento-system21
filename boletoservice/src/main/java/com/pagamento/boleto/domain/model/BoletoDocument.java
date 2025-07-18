package com.pagamento.boleto.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "boletos")
public class BoletoDocument {
    @Id
    private UUID id;
    private String pagador;
    private String beneficiario;
    private BigDecimal valor;
    private LocalDate dataVencimento;
    private LocalDate dataEmissao;
    private BoletoStatus status;
    private String documento;
    private String instrucoes;
    private String localPagamento;
    private String motivoCancelamento;
    private int numeroReemissoes;
    private String boletoOriginalId;
    private String idExterno;
    private DadosTecnicosDocument dadosTecnicos;

    // Construtor padrão necessário para o Spring Data
    public BoletoDocument() {
    }

    // Construtor completo
    public BoletoDocument(
        UUID id,
        String pagador,
        String beneficiario,
        BigDecimal valor,
        LocalDate dataVencimento,
        LocalDate dataEmissao,
        BoletoStatus status,
        String documento,
        String instrucoes,
        String localPagamento,
        String motivoCancelamento,
        int numeroReemissoes,
        String boletoOriginalId,
        String idExterno,
        DadosTecnicosDocument dadosTecnicos
    ) {
        this.id = id;
        this.pagador = pagador;
        this.beneficiario = beneficiario;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.dataEmissao = dataEmissao;
        this.status = status;
        this.documento = documento;
        this.instrucoes = instrucoes;
        this.localPagamento = localPagamento;
        this.motivoCancelamento = motivoCancelamento;
        this.numeroReemissoes = numeroReemissoes;
        this.boletoOriginalId = boletoOriginalId;
        this.idExterno = idExterno;
        this.dadosTecnicos = dadosTecnicos;
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID uuid) {
        this.id = uuid;
    }

    public String getPagador() {
        return pagador;
    }

    public void setPagador(String pagador) {
        this.pagador = pagador;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public BoletoStatus getStatus() {
        return status;
    }

    public void setStatus(BoletoStatus status) {
        this.status = status;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getInstrucoes() {
        return instrucoes;
    }

    public void setInstrucoes(String instrucoes) {
        this.instrucoes = instrucoes;
    }

    public String getLocalPagamento() {
        return localPagamento;
    }

    public void setLocalPagamento(String localPagamento) {
        this.localPagamento = localPagamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public int getNumeroReemissoes() {
        return numeroReemissoes;
    }

    public void setNumeroReemissoes(int numeroReemissoes) {
        this.numeroReemissoes = numeroReemissoes;
    }

    public String getBoletoOriginalId() {
        return boletoOriginalId;
    }

    public void setBoletoOriginalId(String boletoOriginalId) {
        this.boletoOriginalId = boletoOriginalId;
    }

    public String getIdExterno() {
        return idExterno;
    }

    public void setIdExterno(String idExterno) {
        this.idExterno = idExterno;
    }

    public DadosTecnicosDocument getDadosTecnicos() {
        return dadosTecnicos;
    }

    public void setDadosTecnicos(DadosTecnicosDocument dadosTecnicos) {
        this.dadosTecnicos = dadosTecnicos;
    }

    // Classe interna para dados técnicos
    public static class DadosTecnicosDocument {
        private String codigoBarras;
        private String qrCode;
        private String linhaDigitavel;

        public DadosTecnicosDocument() {
        }

        public DadosTecnicosDocument(String codigoBarras, String qrCode, String linhaDigitavel) {
            this.codigoBarras = codigoBarras;
            this.qrCode = qrCode;
            this.linhaDigitavel = linhaDigitavel;
        }

        public String getCodigoBarras() {
            return codigoBarras;
        }

        public void setCodigoBarras(String codigoBarras) {
            this.codigoBarras = codigoBarras;
        }

        public String getQrCode() {
            return qrCode;
        }

        public void setQrCode(String qrCode) {
            this.qrCode = qrCode;
        }

        public String getLinhaDigitavel() {
            return linhaDigitavel;
        }

        public void setLinhaDigitavel(String linhaDigitavel) {
            this.linhaDigitavel = linhaDigitavel;
        }

		public String getNossoNumero() {
			// TODO Auto-generated method stub
			return null;
		}
    }
}
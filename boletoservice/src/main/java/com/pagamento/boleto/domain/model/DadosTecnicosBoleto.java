package com.pagamento.boleto.domain.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class DadosTecnicosBoleto {
    private String codigoBarras;
    private String linhaDigitavel;
    private String qrCode;
    private String nossoNumero;

    // Construtor padrão necessário para JPA
    public DadosTecnicosBoleto() {}

    public DadosTecnicosBoleto(String codigoBarras, String linhaDigitavel, String qrCode, String nossoNumero) {
        this.codigoBarras = codigoBarras;
        this.linhaDigitavel = linhaDigitavel;
        this.qrCode = qrCode;
        this.nossoNumero = nossoNumero;
    }

    // Getters
    public String getCodigoBarras() { return codigoBarras; }
    public String getLinhaDigitavel() { return linhaDigitavel; }
    public String getQrCode() { return qrCode; }
    public String getNossoNumero() { return nossoNumero; }
}
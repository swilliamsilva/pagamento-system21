package com.pagamento.boleto.application.mapper;

import java.util.Objects;

public final class DadosTecnicos {
    private final String codigoBarras;
    private final String qrCode;
    private final String linhaDigitavel;

    public DadosTecnicos(String codigoBarras, String qrCode, String linhaDigitavel) {
        this.codigoBarras = codigoBarras;
        this.qrCode = qrCode;
        this.linhaDigitavel = linhaDigitavel;
    }

    public String codigoBarras() {
        return codigoBarras;
    }

    public String qrCode() {
        return qrCode;
    }

    public String linhaDigitavel() {
        return linhaDigitavel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DadosTecnicos that = (DadosTecnicos) o;
        return Objects.equals(codigoBarras, that.codigoBarras) &&
                Objects.equals(qrCode, that.qrCode) &&
                Objects.equals(linhaDigitavel, that.linhaDigitavel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoBarras, qrCode, linhaDigitavel);
    }

    @Override
    public String toString() {
        return "DadosTecnicos[" +
                "codigoBarras='" + codigoBarras + '\'' +
                ", qrCode='" + qrCode + '\'' +
                ", linhaDigitavel='" + linhaDigitavel + '\'' +
                ']';
    }
}
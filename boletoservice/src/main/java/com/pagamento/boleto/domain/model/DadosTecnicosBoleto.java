package com.pagamento.boleto.domain.model;

import jakarta.persistence.Embeddable;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Embeddable
public class DadosTecnicosBoleto {
    private String codigoBarras;
    private String linhaDigitavel;
    private String qrCode;
    private String nossoNumero;

    // Construtor padrão necessário para JPA
    public DadosTecnicosBoleto() {}

    public DadosTecnicosBoleto(
        String codigoBarras, 
        String linhaDigitavel, 
        String qrCode, 
        String nossoNumero
    ) {
        setCodigoBarras(codigoBarras);
        setLinhaDigitavel(linhaDigitavel);
        setQrCode(qrCode);
        setNossoNumero(nossoNumero);
    }

    // Getters
    public String getCodigoBarras() { 
        return codigoBarras; 
    }
    
    public String getLinhaDigitavel() { 
        return linhaDigitavel; 
    }
    
    public String getQrCode() { 
        return qrCode; 
    }
    
    public String getNossoNumero() { 
        return nossoNumero; 
    }

    // Setters com validação aprimorada
    public void setCodigoBarras(String codigoBarras) {
        validateCampo(codigoBarras, "Código de barras");
        this.codigoBarras = codigoBarras;
    }

    public void setLinhaDigitavel(String linhaDigitavel) {
        validateCampo(linhaDigitavel, "Linha digitável");
        this.linhaDigitavel = linhaDigitavel;
    }

    public void setQrCode(String qrCode) {
        validateCampo(qrCode, "QR Code");
        this.qrCode = qrCode;
    }

    public void setNossoNumero(String nossoNumero) {
        validateCampo(nossoNumero, "Nosso número");
        this.nossoNumero = nossoNumero;
    }

    // Método auxiliar para validação
    private void validateCampo(String valor, String nomeCampo) {
        Validate.notNull(valor, nomeCampo + " não pode ser nulo");
        Validate.isTrue(StringUtils.isNotBlank(valor), nomeCampo + " não pode ser vazio ou em branco");
    }

    // Métodos de acesso simplificados (opcionais)
    public String codigoBarras() {
        return this.codigoBarras;
    }
    
    public String linhaDigitavel() {
        return this.linhaDigitavel;
    }
    
    public String qrCode() {
        return this.qrCode;
    }
    
    public String nossoNumero() {
        return this.nossoNumero;
    }

    // Equals e HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DadosTecnicosBoleto that = (DadosTecnicosBoleto) o;
        return Objects.equals(codigoBarras, that.codigoBarras) &&
                Objects.equals(linhaDigitavel, that.linhaDigitavel) &&
                Objects.equals(qrCode, that.qrCode) &&
                Objects.equals(nossoNumero, that.nossoNumero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoBarras, linhaDigitavel, qrCode, nossoNumero);
    }
}
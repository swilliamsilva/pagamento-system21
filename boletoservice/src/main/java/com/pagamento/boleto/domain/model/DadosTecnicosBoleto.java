package com.pagamento.boleto.domain.model;

import jakarta.persistence.Embeddable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

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

    // Setters com validação
    public void setCodigoBarras(String codigoBarras) {
        Validate.notBlank(codigoBarras, "Código de barras não pode ser vazio");
        this.codigoBarras = codigoBarras;
    }

    public void setLinhaDigitavel(String linhaDigitavel) {
        Validate.notBlank(linhaDigitavel, "Linha digitável não pode ser vazia");
        this.linhaDigitavel = linhaDigitavel;
    }

    public void setQrCode(String qrCode) {
        Validate.notBlank(qrCode, "QR Code não pode ser vazio");
        this.qrCode = qrCode;
    }

    public void setNossoNumero(String nossoNumero) {
        Validate.notBlank(nossoNumero, "Nosso número não pode ser vazio");
        this.nossoNumero = nossoNumero;
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
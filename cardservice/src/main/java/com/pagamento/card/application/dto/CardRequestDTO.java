package com.pagamento.card.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public class CardRequestDTO {
    
    @NotBlank(message = "Número do cartão é obrigatório")
    @Pattern(regexp = "^\\d{13,19}$", message = "Número do cartão inválido")
    private String numeroCartao;
    
    @NotBlank(message = "Bandeira é obrigatória")
    private String bandeira;
    
    @NotBlank(message = "Data de validade é obrigatória")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Formato deve ser MM/YY")
    private String dataValidade;
    
    @NotBlank(message = "CVV é obrigatório")
    @Pattern(regexp = "^\\d{3,4}$", message = "CVV inválido")
    private String cvv;
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;
    
    private String nomeTitular;
    private Integer parcelas = 1;
    
    // Getters e Setters
    public String getNumeroCartao() { return numeroCartao; }
    public void setNumeroCartao(String numeroCartao) { this.numeroCartao = numeroCartao; }
    
    public String getBandeira() { return bandeira; }
    public void setBandeira(String bandeira) { this.bandeira = bandeira; }
    
    public String getDataValidade() { return dataValidade; }
    public void setDataValidade(String dataValidade) { this.dataValidade = dataValidade; }
    
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    
    public String getNomeTitular() { return nomeTitular; }
    public void setNomeTitular(String nomeTitular) { this.nomeTitular = nomeTitular; }
    
    public Integer getParcelas() { return parcelas; }
    public void setParcelas(Integer parcelas) { 
        this.parcelas = parcelas != null && parcelas > 0 ? parcelas : 1; 
    }
}
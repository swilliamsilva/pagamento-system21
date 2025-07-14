package com.pagamento.card.model;

import jakarta.validation.constraints.*;

public class Card {
    @NotBlank(message = "ID do cartão é obrigatório")
    private String id;
    
    @NotBlank(message = "Nome do titular é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Nome contém caracteres inválidos")
    private String nomeTitular;
    
    @NotBlank(message = "Número do cartão é obrigatório")
    @Pattern(regexp = "^\\d{13,19}$", message = "Número do cartão inválido")
    private String numero;
    
    @NotBlank(message = "Data de validade é obrigatória")
    @Pattern(regexp = "^(0[1-9]|1[0-2])\\/?(\\d{2})$", 
             message = "Formato deve ser MM/AA")
    private String validade;
    
    @NotBlank(message = "CVV é obrigatório")
    @Size(min = 3, max = 4, message = "CVV deve ter 3 ou 4 dígitos")
    @Pattern(regexp = "^\\d+$", message = "CVV deve conter apenas números")
    private String cvv;

    public Card() {}

    public Card(String id, String nomeTitular, String numero, String validade, String cvv) {
        this.id = id;
        this.nomeTitular = nomeTitular;
        this.numero = numero;
        this.validade = validade;
        this.cvv = cvv;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNomeTitular() { return nomeTitular; }
    public void setNomeTitular(String nomeTitular) { this.nomeTitular = nomeTitular; }
    
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    
    public String getValidade() { return validade; }
    public void setValidade(String validade) { this.validade = validade; }
    
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    @Override
    public String toString() {
        return "Card{" +
               "id='" + id + '\'' +
               ", nomeTitular='" + nomeTitular + '\'' +
               ", numero='" + maskCardNumber() + '\'' +
               ", validade='" + validade + '\'' +
               '}';
    }

    private String maskCardNumber() {
        if (numero == null) {
            return "null";
        }
        
        if (numero.length() <= 4) {
            return "****";
        }
        
        return "****-****-****-" + numero.substring(numero.length() - 4);
    }

    public String maskCardNumberForLog() {
        if (numero == null || numero.length() < 4) {
            return "****";
        }
        return "****-****-****-" + numero.substring(numero.length() - 4);
    }
}
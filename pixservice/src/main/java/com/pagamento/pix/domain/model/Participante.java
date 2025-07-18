package com.pagamento.pix.domain.model;

public class Participante {
    private String nome;
    private String documento; // CPF ou CNPJ
    private String ispb;
    private String agencia;
    private String conta;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    
    public String getIspb() { return ispb; }
    public void setIspb(String ispb) { this.ispb = ispb; }
    
    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = agencia; }
    
    public String getConta() { return conta; }
    public void setConta(String conta) { this.conta = conta; }
}
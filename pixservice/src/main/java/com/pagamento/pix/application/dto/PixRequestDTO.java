package com.pagamento.pix.application.dto;

import java.math.BigDecimal;

public class PixRequestDTO {
    private String chaveDestino;
    private String tipo;
    private BigDecimal valor;
    private String documentoPagador;
    private String nomePagador;
    private String nomeRecebedor;
    private String ispbRecebedor;
    private String agenciaRecebedor;
    private String contaRecebedor;
    
    // Getters e Setters
    public String getChaveDestino() {
        return chaveDestino;
    }

    public void setChaveDestino(String chaveDestino) {
        this.chaveDestino = chaveDestino;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDocumentoPagador() {
        return documentoPagador;
    }

    public void setDocumentoPagador(String documentoPagador) {
        this.documentoPagador = documentoPagador;
    }

    public String getNomePagador() {
        return nomePagador;
    }

    public void setNomePagador(String nomePagador) {
        this.nomePagador = nomePagador;
    }

    public String getNomeRecebedor() {
        return nomeRecebedor;
    }

    public void setNomeRecebedor(String nomeRecebedor) {
        this.nomeRecebedor = nomeRecebedor;
    }

    public String getIspbRecebedor() {
        return ispbRecebedor;
    }

    public void setIspbRecebedor(String ispbRecebedor) {
        this.ispbRecebedor = ispbRecebedor;
    }

    public String getAgenciaRecebedor() {
        return agenciaRecebedor;
    }

    public void setAgenciaRecebedor(String agenciaRecebedor) {
        this.agenciaRecebedor = agenciaRecebedor;
    }

    public String getContaRecebedor() {
        return contaRecebedor;
    }

    public void setContaRecebedor(String contaRecebedor) {
        this.contaRecebedor = contaRecebedor;
    }

	public String getSolicitacaoId() {
		// TODO Auto-generated method stub
		return null;
	}
}
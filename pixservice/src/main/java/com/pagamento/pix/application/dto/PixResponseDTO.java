package com.pagamento.pix.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PixResponseDTO {
    private String id;
    private String chaveDestino;
    private String tipo;
    private BigDecimal valor;
    private LocalDateTime dataTransacao;
    private String status;
    private String bacenId;
    private String nomeRecebedor;
    private String ispbRecebedor;
    private String agenciaRecebedor;
    private String contaRecebedor;
    
    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public LocalDateTime getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDateTime dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBacenId() {
        return bacenId;
    }

    public void setBacenId(String bacenId) {
        this.bacenId = bacenId;
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
}
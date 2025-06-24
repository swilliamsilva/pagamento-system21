/* ========================================================
# Classe: PixRequestDTO
# Módulo: pix-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Dados de entrada para criação de Pix.
# ======================================================== */

package com.pagamento.pix.application.dto;

import java.math.BigDecimal;

public class PixRequestDTO {
    private String chaveDestino;
    private String tipo; // Campo adicionado
    private BigDecimal valor;

    // Getters/Setters corretos:
    public String getChaveDestino() { return chaveDestino; }
    public void setChaveDestino(String chaveDestino) { this.chaveDestino = chaveDestino; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
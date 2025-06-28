/* ========================================================
# Classe: PixResponseDTO
# Módulo: pix-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Dados de retorno da operação Pix.
# ======================================================== */

package com.pagamento.pix.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PixResponseDTO {
    private String id;
    private String chaveDestino; // Tipo corrigido
    private String tipo; // Campo adicionado
    private BigDecimal valor;
    private LocalDateTime dataTransacao;

    // Getters/Setters:
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getChaveDestino() { return chaveDestino; }
    public void setChaveDestino(String chaveDestino) { this.chaveDestino = chaveDestino; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    
    public LocalDateTime getDataTransacao() { return dataTransacao; }
    public void setDataTransacao(LocalDateTime dataTransacao) { this.dataTransacao = dataTransacao; }
}
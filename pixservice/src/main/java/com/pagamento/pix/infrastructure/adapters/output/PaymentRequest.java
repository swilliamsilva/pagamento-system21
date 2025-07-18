package com.pagamento.pix.infrastructure.adapters.output;

import java.math.BigDecimal;

public class PaymentRequest {
    private final String chaveOrigem;
    private final String chaveDestino;
    private final BigDecimal valor;

    public PaymentRequest(String chaveOrigem, String chaveDestino, BigDecimal valor) {
        this.chaveOrigem = chaveOrigem;
        this.chaveDestino = chaveDestino;
        this.valor = valor;
    }

    // Getters
    public String getChaveOrigem() {
        return chaveOrigem;
    }

    public String getChaveDestino() {
        return chaveDestino;
    }

    public BigDecimal getValor() {
        return valor;
    }
}
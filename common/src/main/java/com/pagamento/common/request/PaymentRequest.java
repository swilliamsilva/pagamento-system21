package com.pagamento.common.request;

import java.math.BigDecimal;

public class PaymentRequest {
    private String userId;
    private String tipoPagamento;
    private BigDecimal valor;

    private PaymentRequest(Builder builder) {
        this.userId = builder.userId;
        this.tipoPagamento = builder.tipoPagamento;
        this.valor = builder.valor;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUserId() {
        return userId;
    }

    public String getTipoPagamento() {
        return tipoPagamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public static class Builder {
        private String userId;
        private String tipoPagamento;
        private BigDecimal valor;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder tipoPagamento(String tipoPagamento) {
            this.tipoPagamento = tipoPagamento;
            return this;
        }

        public Builder valor(BigDecimal valor) {
            this.valor = valor;
            return this;
        }

        public PaymentRequest build() {
            return new PaymentRequest(this);
        }
    }
}



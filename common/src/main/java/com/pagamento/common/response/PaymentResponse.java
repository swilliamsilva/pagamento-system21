package com.pagamento.common.response;

import java.math.BigDecimal;

public class PaymentResponse {
    private String transactionId;
    private String status;
    private String paymentType;
    private BigDecimal amount;

    private PaymentResponse(Builder builder) {
        this.transactionId = builder.transactionId;
        this.status = builder.status;
        this.paymentType = builder.paymentType;
        this.amount = builder.amount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public static class Builder {
        private String transactionId;
        private String status;
        private String paymentType;
        private BigDecimal amount;

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder paymentType(String paymentType) {
            this.paymentType = paymentType;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public PaymentResponse build() {
            return new PaymentResponse(this);
        }
    }
}



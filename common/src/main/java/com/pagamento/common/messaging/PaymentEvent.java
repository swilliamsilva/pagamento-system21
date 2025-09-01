package com.pagamento.common.messaging;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentEvent {
    private String transactionId;
    private String paymentType;
    private BigDecimal amount;
    private Instant timestamp;
    private String status;

    private PaymentEvent(Builder builder) {
        this.transactionId = builder.transactionId;
        this.paymentType = builder.paymentType;
        this.amount = builder.amount;
        this.timestamp = builder.timestamp;
        this.status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public static class Builder {
        private String transactionId;
        private String paymentType;
        private BigDecimal amount;
        private Instant timestamp;
        private String status;

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
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

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public PaymentEvent build() {
            if (transactionId == null || transactionId.isBlank()) {
                throw new IllegalArgumentException("Transaction ID não pode ser nulo ou vazio");
            }
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Valor deve ser positivo");
            }
            if (status == null || status.isBlank()) {
                status = "PENDENTE";
            }
            return new PaymentEvent(this);
        }
    }
}



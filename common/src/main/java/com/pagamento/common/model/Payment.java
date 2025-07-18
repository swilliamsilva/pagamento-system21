package com.pagamento.common.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Payment {
    private String transactionId;
    private String userId;
    private String paymentType;
    private BigDecimal amount;
    private Instant createdAt;
    private String status;

    // Construtor vazio necessário para frameworks
    public Payment() {}

    // Construtor privado para o Builder
    private Payment(Builder builder) {
        this.transactionId = builder.transactionId;
        this.userId = builder.userId;
        this.paymentType = builder.paymentType;
        this.amount = builder.amount;
        this.createdAt = builder.createdAt;
        this.status = builder.status;
    }

    // Método builder estático
    public static Builder builder() {
        return new Builder();
    }

    // Getters e Setters (necessários para o mapper)
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Classe Builder interna
    public static class Builder {
        private String transactionId;
        private String userId;
        private String paymentType;
        private BigDecimal amount;
        private Instant createdAt;
        private String status;

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
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

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }
}
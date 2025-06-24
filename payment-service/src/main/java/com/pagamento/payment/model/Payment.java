package com.pagamento.payment.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Payment {
    private String transactionId;
    private String userId;
    private String paymentType;
    private BigDecimal amount;
    private Instant createdAt;
    private String status; // Adicionado para suportar resposta da API

    // Construtor padrão
    public Payment() {}

    // Builder pattern manual
    public static Payment builder() {
        return new Payment();
    }

    public Payment transactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public Payment userId(String userId) {
        this.userId = userId;
        return this;
    }

    public Payment paymentType(String paymentType) {
        this.paymentType = paymentType;
        return this;
    }

    public Payment amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public Payment createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Payment status(String status) {
        this.status = status;
        return this;
    }

    public Payment build() {
        return this;
    }

    // Getters e Setters
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

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTipo() {
		// TODO Auto-generated method stub
		return null;
	}

	public BigDecimal getValor() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTipo(Object tipo) {
		// TODO Auto-generated method stub
		
	}

	public void setValor(BigDecimal valor) {
		// TODO Auto-generated method stub
		
	}

	public Object getData() {
		// TODO Auto-generated method stub
		return null;
	}
}
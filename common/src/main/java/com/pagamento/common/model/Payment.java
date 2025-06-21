// ==========================
// MODEL: Payment.java (completo)
// ==========================
package com.pagamento.common.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Orquestrador do pagamento
 *
 * Representa uma transação de pagamento persistida.
 */
public class Payment {
    private Long id;
    private String transactionId;
    private String userId;
    private String paymentType; // PIX, BOLETO, CARTAO
    private BigDecimal amount;
    private Date createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

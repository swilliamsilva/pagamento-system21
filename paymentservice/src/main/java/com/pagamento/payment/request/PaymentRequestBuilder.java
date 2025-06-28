package com.pagamento.payment.request;

import java.math.BigDecimal;

import com.pagamento.common.request.PaymentRequest;

// PaymentRequestBuilder.java
public class PaymentRequestBuilder {
    private String userId;
    private BigDecimal amount;
    
    public PaymentRequestBuilder withUserId(String userId) {
        this.userId = userId;
        return this;
    }
    
    public PaymentRequest build() {
        return new PaymentRequest(userId, userId, amount);
    }
}
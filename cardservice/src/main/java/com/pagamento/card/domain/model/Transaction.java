package com.pagamento.card.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class Transaction {
    private String id;
    private String cardNumber;
    private String brand;
    private String expiryDate;
    private String cvv;
    private BigDecimal amount;
    private String cardHolder;
    private Integer installments;
    private String status;
    private String authorizationCode;
    private String message;
}
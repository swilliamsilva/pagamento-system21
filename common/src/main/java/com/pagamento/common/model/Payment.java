/***
 * Orquestrador do pagamento
 * 
 */

package com.pagamento.common.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Representa uma transação de pagamento persistida.
 */
public class Payment {
    private Long id;
    private String transactionId;
    private String paymentType; // PIX, BOLETO, CARTAO
    private BigDecimal amount;
    private Date createdAt;

    // Getters e Setters
}

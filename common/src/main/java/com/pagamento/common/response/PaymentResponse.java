package com.pagamento.common.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class PaymentResponse {
    private String transactionId;
    private String status;
    private String paymentType;
    private BigDecimal amount;
}
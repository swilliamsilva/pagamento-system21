package com.pagamento.common.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Getter
@Builder
public class PaymentDTO {
    @NotBlank(message = "Transaction ID não pode ser vazio")
    private String transactionId;
    
    @NotBlank(message = "Tipo de pagamento não pode ser vazio")
    private String paymentType;
    
    @NotNull(message = "Valor não pode ser nulo")
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal amount;
}


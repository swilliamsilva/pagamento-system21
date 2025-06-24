package com.pagamento.common.dto;


import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class PaymentDTO {
    private String transactionId;
    private String paymentType;
    private BigDecimal amount;
	public static Object builder() {
		// TODO Auto-generated method stub
		return null;
	}
}
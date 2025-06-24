package com.pagamento.common.response;



import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class PaymentResponse {
    private String transactionId;
    private String status;
    private String paymentType;
    private BigDecimal amount;
	public PaymentResponse(String string, Object object, Object object2, Object object3) {
		// TODO Auto-generated constructor stub
	}
	public static Object builder() {
		// TODO Auto-generated method stub
		return null;
	}
}

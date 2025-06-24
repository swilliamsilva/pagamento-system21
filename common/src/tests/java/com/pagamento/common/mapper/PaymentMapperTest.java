// ==========================
// TEST: PaymentMapperTest.java
// ==========================
package com.pagamento.common.mapper;

import com.pagamento.common.dto.PaymentDTO;
import com.pagamento.common.dto.PaymentRequest;
import com.pagamento.common.dto.PaymentResponse;
import com.pagamento.common.model.Payment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentMapperTest {

    @Test
    public void deveConverterRequestParaEntidade() {
        PaymentRequest request = new PaymentRequest("user001", "CARTAO", new BigDecimal("250.00"));
        Payment entity = PaymentMapper.toEntity(request);

        assertEquals("user001", entity.getUserId());
        assertEquals("CARTAO", entity.getPaymentType());
        assertEquals(new BigDecimal("250.00"), entity.getAmount());
        assertNotNull(entity.getTransactionId());
        assertNotNull(entity.getCreatedAt());
    }

    @Test
    public void deveConverterEntidadeParaDTO() {
        Payment entity = new Payment();
        entity.setTransactionId("tx123");
        entity.setPaymentType("PIX");
        entity.setAmount(new BigDecimal("75.00"));

        PaymentDTO dto = PaymentMapper.toDto(entity);

        assertEquals("tx123", dto.transactionId());
        assertEquals("PIX", dto.tipoPagamento());
        assertEquals(new BigDecimal("75.00"), dto.valor());
    }

    @Test
    public void deveConverterEntidadeParaResponse() {
        Payment entity = new Payment();
        entity.setTransactionId("tx456");
        entity.setPaymentType("BOLETO");
        entity.setAmount(new BigDecimal("300.00"));

        PaymentResponse response = PaymentMapper.toResponse(entity);

        assertEquals("tx456", response.idTransacao());
        assertEquals("BOLETO", response.tipoPagamento());
        assertEquals(new BigDecimal("300.00"), response.valor());
        assertEquals("APROVADO", response.status());
    }
}

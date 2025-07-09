package com.pagamento.payment.dto.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.pagamento.common.request.PaymentRequest;
import com.pagamento.common.response.PaymentResponse;
import com.pagamento.payment.enums.PaymentType;
import com.pagamento.payment.model.Payment;

public class PaymentMapperTest {

    private final PaymentMapper mapper = new PaymentMapper();

    @Test
    public void deveConverterRequestParaEntidade() {
        PaymentRequest request = new PaymentRequest("user001", "PIX", new BigDecimal("250.00"));
        PaymentType paymentType = PaymentType.PIX;
        
        Payment entity = mapper.toEntity(request, paymentType);

        assertEquals("user001", entity.getUserId());
        assertEquals(PaymentType.PIX, entity.getTipoPagamento());
        assertEquals(new BigDecimal("250.00"), entity.getValor());
        assertNotNull(entity.getIdTransacao());
        assertNotNull(entity.getData());
    }

    @Test
    public void deveConverterEntidadeParaResponse() {
        Payment entity = new Payment();
        entity.setIdTransacao("tx123");
        entity.setTipoPagamento(PaymentType.PIX);
        entity.setValor(new BigDecimal("75.00"));
        entity.setData(Instant.now());
        entity.setStatus("APROVADO");

        PaymentResponse response = mapper.toResponse(entity);

        assertEquals("APROVADO", response.status());
        assertEquals("tx123", response.idTransacao());
        assertEquals(new BigDecimal("75.00"), response.valor());
        assertEquals("PIX", response.tipoPagamento());
        assertNotNull(response.data());
    }
}
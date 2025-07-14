package com.pagamento.card.application.mapper;

import com.pagamento.card.application.dto.CardRequestDTO;
import com.pagamento.card.application.dto.CardResponseDTO;
import com.pagamento.card.domain.enums.PaymentStatus;
import com.pagamento.card.domain.model.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CardMapperTest {

    @Test
    void shouldConvertRequestToEntityCorrectly() {
        CardRequestDTO dto = new CardRequestDTO();
        dto.setNumeroCartao("4111111111111111");
        dto.setBandeira("VISA");
        dto.setDataValidade("12/25");
        dto.setCvv("123");
        dto.setValor(BigDecimal.valueOf(100.00));
        dto.setNomeTitular("John Doe");
        dto.setParcelas(3);

        Transaction entity = CardMapper.toTransactionEntity(dto);

        assertNotNull(entity);
        assertEquals("4111111111111111", entity.getCardNumber());
        assertEquals("VISA", entity.getBrand());
        assertEquals("12/25", entity.getExpiryDate());
        assertEquals("123", entity.getCvv());
        assertEquals(BigDecimal.valueOf(100.00), entity.getAmount());
        assertEquals("John Doe", entity.getCardHolder());
        assertEquals(3, entity.getInstallments());
    }

    @Test
    void shouldConvertEntityToResponseCorrectly() {
        Transaction entity = Transaction.builder()
                .id("TXN-12345")
                .brand("MASTERCARD")
                .status("APPROVED")
                .amount(BigDecimal.valueOf(200.00))
                .authorizationCode("AUTH-9876")
                .message("Transação aprovada")
                .build();

        CardResponseDTO dto = CardMapper.toResponseDTO(entity);

        assertNotNull(dto);
        assertEquals("TXN-12345", dto.getTransactionId());
        assertEquals("MASTERCARD", dto.getBandeira());
        assertEquals(PaymentStatus.APPROVED, dto.getStatus());
        assertEquals(BigDecimal.valueOf(200.00), dto.getValor());
        assertEquals("AUTH-9876", dto.getCodigoAutorizacao());
        assertEquals("Transação aprovada", dto.getMensagem());
    }

    @Test
    void shouldHandleNullInputsSafely() {
        assertNull(CardMapper.toTransactionEntity(null));
        assertNull(CardMapper.toResponseDTO(null));
        
        Transaction entity = new Transaction();
        CardResponseDTO dto = new CardResponseDTO();
        CardMapper.updateEntityFromResponse(null, null); // Não deve lançar exceção
        CardMapper.updateEntityFromResponse(entity, null);
        CardMapper.updateEntityFromResponse(null, dto);
    }
}
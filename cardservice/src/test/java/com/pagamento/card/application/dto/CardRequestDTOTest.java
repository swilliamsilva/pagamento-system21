package com.pagamento.card.application.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CardRequestDTOTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void dtoValidoDevePassarNaValidacao() {
        CardRequestDTO dto = new CardRequestDTO();
        dto.setNumero("4111111111111111");
        dto.setNomeTitular("Maria Silva");
        dto.setValidade("12/25");
        dto.setCvv("123");
        
        Set violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void deveRejeitarNumeroCartaoInvalido() {
        CardRequestDTO dto = new CardRequestDTO();
        dto.setNumero("4111-1111-1111-1111"); // Formato inválido
        dto.setNomeTitular("Maria Silva");
        dto.setValidade("12/25");
        dto.setCvv("123");
        
        Set violations = validator.validate(dto);
        assertEquals(1, violations.size());
    }
}
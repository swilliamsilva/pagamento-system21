// ==========================
// TEST: PaymentDTOValidationTest.java
// ==========================
package com.pagamento.common.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentDTOValidationTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void deveAceitarDTOValido() {
        PaymentRequest dto = new PaymentRequest("user123", "PIX", new BigDecimal("100.00"));
        Set violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void deveRejeitarValorNulo() {
        PaymentRequest dto = new PaymentRequest("user123", "PIX", null);
        Set violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void deveRejeitarTipoPagamentoVazio() {
        PaymentRequest dto = new PaymentRequest("user123", "", new BigDecimal("10.00"));
        Set violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}
// ==========================
// 
// TEST: UserDTOValidationTest.java
// ==========================
package com.pagamento.common.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserDTOValidationTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void deveValidarDTOCorretamente() {
        UserDTO dto = new UserDTO("123", "João", "joao@email.com", "12345678909");
        Set violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void deveInvalidarEmail() {
        UserDTO dto = new UserDTO("123", "João", "email-invalido", "12345678909");
        Set violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void deveInvalidarCPF() {
        UserDTO dto = new UserDTO("123", "João", "joao@email.com", "00000000000");
        Set violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}
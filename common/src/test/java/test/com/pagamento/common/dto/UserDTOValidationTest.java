package test.com.pagamento.common.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pagamento.common.dto.UserDTO;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOValidationTest {  // Removed 'public' modifier

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void deveValidarDTOCorretamente() {
        UserDTO dto = new UserDTO("123", "João", "joao@email.com", "12345678909");
        Set<?> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void deveInvalidarEmail() {
        UserDTO dto = new UserDTO("123", "João", "email-invalido", "12345678909");
        Set<?> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void deveInvalidarCPF() {
        UserDTO dto = new UserDTO("123", "João", "joao@email.com", "00000000000");
        Set<?> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}
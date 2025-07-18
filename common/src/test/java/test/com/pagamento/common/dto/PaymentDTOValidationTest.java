package test.com.pagamento.common.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pagamento.common.request.PaymentRequest;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PaymentDTOValidationTest {  // Removed 'public' modifier

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void deveAceitarDTOValido() {
        PaymentRequest dto = new PaymentRequest("user123", "PIX", new BigDecimal("100.00"));
        Set<?> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void deveRejeitarValorNulo() {
        PaymentRequest dto = new PaymentRequest("user123", "PIX", null);
        Set<?> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void deveRejeitarTipoPagamentoVazio() {
        PaymentRequest dto = new PaymentRequest("user123", "", new BigDecimal("10.00"));
        Set<?> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}
package test.com.pagamento.card.model;



import com.pagamento.card.model.Card;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CardValidationTest {
    private Validator validator;
    private Card validCard;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        validCard = new Card(
            "card-123", 
            "Maria Silva", 
            "5555666677778888", 
            "12/25", 
            "123"
        );
    }

    @Test
    void cartaoValidoDevePassarNaValidacao() {
        Set<ConstraintViolation<Card>> violations = validator.validate(validCard);
        assertTrue(violations.isEmpty());
    }

    @Test
    void numeroCartaoInvalidoDeveFalhar() {
        validCard.setNumero("1234-5678-9012");
        Set<ConstraintViolation<Card>> violations = validator.validate(validCard);
        assertEquals(1, violations.size());
        assertEquals("Número do cartão inválido", violations.iterator().next().getMessage());
    }

    @Test
    void formatoValidadeInvalidoDeveFalhar() {
        validCard.setValidade("13/25");
        Set<ConstraintViolation<Card>> violations = validator.validate(validCard);
        assertEquals(1, violations.size());
        assertEquals("Formato deve ser MM/AA", violations.iterator().next().getMessage());
    }

    @Test
    void nomeTitularInvalidoDeveFalhar() {
        validCard.setNomeTitular("M4r!@ Silva");
        Set<ConstraintViolation<Card>> violations = validator.validate(validCard);
        assertEquals(1, violations.size());
        assertEquals("Nome contém caracteres inválidos", violations.iterator().next().getMessage());
    }

    @ParameterizedTest
    @CsvSource({
        "null, '****'",
        "'', '****'",
        "'123', '****'",
        "'1234', '****-****-****-1234'",
        "'1234567890123456', '****-****-****-3456'",
        "'98765432109876543210', '****-****-****-3210'"
    })
    void toStringDeveMascararNumeroCartao(String numero, String expected) {
        Card card = new Card();
        card.setId("1");
        card.setNomeTitular("Titular");
        card.setValidade("12/25");
        card.setCvv("123");
        
        if (!"null".equals(numero)) {
            card.setNumero(numero);
        } else {
            card.setNumero(null);
        }
        
        String resultado = card.toString();
        assertTrue(resultado.contains("numero='" + expected + "'"), 
                  "Esperado: " + expected + ", Obtido: " + resultado);
    }
}
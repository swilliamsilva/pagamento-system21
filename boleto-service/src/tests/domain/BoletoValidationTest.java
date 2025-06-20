package tests.boletoservice.domain;

import com.pagamento.boleto.domain.service.BoletoValidation;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class BoletoValidationTest {

    private BoletoValidation validator;

    @BeforeEach
    void setup() {
        validator = new BoletoValidation();
    }

    @Test
    void deveAceitarValorPositivo() {
        assertDoesNotThrow(() -> validator.validarValor(100.0));
    }

    @Test
    void deveLancarErroParaValorZero() {
        assertThrows(IllegalArgumentException.class, () -> validator.validarValor(0));
    }
}

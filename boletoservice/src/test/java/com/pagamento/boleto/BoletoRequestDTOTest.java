package com.pagamento.boleto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BoletoRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidRequest() {
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente A",
            "Beneficiário B",
            BigDecimal.valueOf(100.00),
            LocalDate.now().plusDays(10),
            "DOC-123",
            "Instruções de pagamento",
            "Banco XYZ"
        );
        
        Set<ConstraintViolation<BoletoRequestDTO>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Não deveria haver violações de validação");
    }

    @Test
    void shouldFailWhenPagadorIsBlank() {
        BoletoRequestDTO request = new BoletoRequestDTO(
            "",
            "Beneficiário B",
            BigDecimal.valueOf(100.00),
            LocalDate.now().plusDays(10),
            null, null, null
        );
        
        Set<ConstraintViolation<BoletoRequestDTO>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Pagador é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenBeneficiarioIsBlank() {
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente A",
            "",
            BigDecimal.valueOf(100.00),
            LocalDate.now().plusDays(10),
            null, null, null
        );
        
        Set<ConstraintViolation<BoletoRequestDTO>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Beneficiário é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenValorIsNull() {
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente A",
            "Beneficiário B",
            null,
            LocalDate.now().plusDays(10),
            null, null, null
        );
        
        Set<ConstraintViolation<BoletoRequestDTO>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Valor é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenValorIsNegative() {
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente A",
            "Beneficiário B",
            BigDecimal.valueOf(-50.00),
            LocalDate.now().plusDays(10),
            null, null, null
        );
        
        Set<ConstraintViolation<BoletoRequestDTO>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Valor deve ser positivo", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenDataVencimentoIsNull() {
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente A",
            "Beneficiário B",
            BigDecimal.valueOf(100.00),
            null,
            null, null, null
        );
        
        Set<ConstraintViolation<BoletoRequestDTO>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Data de vencimento é obrigatória", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenDataVencimentoIsPast() {
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente A",
            "Beneficiário B",
            BigDecimal.valueOf(100.00),
            LocalDate.now().minusDays(1),
            null, null, null
        );
        
        Set<ConstraintViolation<BoletoRequestDTO>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Data de vencimento deve ser futura", violations.iterator().next().getMessage());
    }

    @Test
    void shouldUseSimplifiedConstructor() {
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente C",
            "Beneficiário D",
            BigDecimal.valueOf(200.00),
            LocalDate.now().plusDays(15)
        );
        
        assertNull(request.documento());
        assertNull(request.instrucoes());
        assertNull(request.localPagamento());
    }

    @Test
    void shouldGetDefaultEmissaoDate() {
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente E",
            "Beneficiário F",
            BigDecimal.valueOf(300.00),
            LocalDate.now().plusDays(20),
            null, null, null
        );
        
        assertEquals(LocalDate.now(), request.getDataEmissaoOrDefault());
    }

    @Test
    void shouldHandleZeroValueAsInvalid() {
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente G",
            "Beneficiário H",
            BigDecimal.ZERO,
            LocalDate.now().plusDays(5),
            null, null, null
        );
        
        Set<ConstraintViolation<BoletoRequestDTO>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Valor deve ser positivo", violations.iterator().next().getMessage());
    }
}
package com.pagamento.boleto;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.exception.BoletoValidationException;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.service.BoletoValidation;

@DisplayName("Testes de Validação de Boletos")
class BoletoValidationTest {

    private BoletoValidation validator;
    private Boleto boletoValido;

    @BeforeEach
    void setup() {
        validator = new BoletoValidation();
        
        boletoValido = new Boleto();
        boletoValido.setValor(new BigDecimal("100.00"));
        boletoValido.setDataVencimento(LocalDate.now().plusDays(10));
        boletoValido.setStatus(BoletoStatus.EMITIDO);
        boletoValido.setPagador("Cliente A");
        boletoValido.setBeneficiario("Empresa B");
    }

    // Testes para validarEmissao
    @Test
    @DisplayName("Deve aceitar DTO de emissão válido")
    void deveAceitarEmissaoValida() {
        BoletoRequestDTO dto = new BoletoRequestDTO(
            "Pagador", "Beneficiário", new BigDecimal("100.00"), 
            LocalDate.now().plusDays(1), "DOC", "Instruções", "Local"
        );
        assertDoesNotThrow(() -> validator.validarEmissao(dto));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-100"})
    @DisplayName("Deve lançar exceção para valor inválido na emissão")
    void deveLancarErroParaValorInvalidoNaEmissao(String valor) {
        BoletoRequestDTO dto = new BoletoRequestDTO(
            "Pagador", "Beneficiário", new BigDecimal(valor), 
            LocalDate.now().plusDays(1), "DOC", "Instruções", "Local"
        );
        assertThrows(BoletoValidationException.class, () -> validator.validarEmissao(dto));
    }

    @Test
    @DisplayName("Deve lançar exceção para data de vencimento inválida na emissão")
    void deveLancarErroParaDataVencimentoInvalidaNaEmissao() {
        BoletoRequestDTO dto = new BoletoRequestDTO(
            "Pagador", "Beneficiário", new BigDecimal("100.00"), 
            LocalDate.now().minusDays(1), "DOC", "Instruções", "Local"
        );
        assertThrows(BoletoValidationException.class, () -> validator.validarEmissao(dto));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Deve lançar exceção para pagador ou beneficiário vazios na emissão")
    void deveLancarErroParaPagadorOuBeneficiarioVaziosNaEmissao(String valor) {
        BoletoRequestDTO dto1 = new BoletoRequestDTO(
            valor, "Beneficiário", new BigDecimal("100.00"), 
            LocalDate.now().plusDays(1), "DOC", "Instruções", "Local"
        );
        BoletoRequestDTO dto2 = new BoletoRequestDTO(
            "Pagador", valor, new BigDecimal("100.00"), 
            LocalDate.now().plusDays(1), "DOC", "Instruções", "Local"
        );
        
        assertAll(
            () -> assertThrows(BoletoValidationException.class, () -> validator.validarEmissao(dto1)),
            () -> assertThrows(BoletoValidationException.class, () -> validator.validarEmissao(dto2))
        );
    }

    // Testes para validarBoleto
    @Test
    @DisplayName("Deve aceitar boleto válido")
    void deveAceitarBoletoValido() {
        assertDoesNotThrow(() -> validator.validarBoleto(boletoValido));
    }

    @Test
    @DisplayName("Deve lançar exceção para boleto nulo")
    void deveLancarErroParaBoletoNulo() {
        assertThrows(BoletoValidationException.class, () -> validator.validarBoleto(null));
    }

    @Test
    @DisplayName("Deve lançar exceção para data de vencimento nula")
    void deveLancarErroParaDataVencimentoNula() {
        boletoValido.setDataVencimento(null);
        assertThrows(BoletoValidationException.class, () -> validator.validarBoleto(boletoValido));
    }

    @ParameterizedTest
    @EnumSource(value = BoletoStatus.class, names = {"PAGO", "CANCELADO"}, mode = EXCLUDE)
    @DisplayName("Deve aceitar boleto em estados operáveis")
    void deveAceitarBoletoEmEstadosOperaveis(BoletoStatus status) {
        boletoValido.setStatus(status);
        assertDoesNotThrow(() -> validator.validarBoleto(boletoValido));
    }

    @ParameterizedTest
    @EnumSource(value = BoletoStatus.class, names = {"PAGO", "CANCELADO"})
    @DisplayName("Deve lançar exceção para boleto em estados não operáveis")
    void deveLancarErroParaBoletoEmEstadosNaoOperaveis(BoletoStatus status) {
        boletoValido.setStatus(status);
        assertThrows(BoletoValidationException.class, () -> validator.validarBoleto(boletoValido));
    }

    // Testes para validarReemissao
    @ParameterizedTest
    @EnumSource(value = BoletoStatus.class, names = {"EMITIDO", "VENCIDO"})
    @DisplayName("Deve aceitar reemissão para estados permitidos")
    void deveAceitarReemissaoParaEstadosPermitidos(BoletoStatus status) {
        boletoValido.setStatus(status);
        boletoValido.setNumeroReemissoes(2); // Abaixo do limite
        assertDoesNotThrow(() -> validator.validarReemissao(boletoValido));
    }

    @ParameterizedTest
    @EnumSource(value = BoletoStatus.class, names = {"PAGO", "CANCELADO", "REEMITIDO"})
    @DisplayName("Deve lançar exceção para reemissão em estados não permitidos")
    void deveLancarErroParaReemissaoEmEstadosNaoPermitidos(BoletoStatus status) {
        boletoValido.setStatus(status);
        assertThrows(BoletoValidationException.class, () -> validator.validarReemissao(boletoValido));
    }

    @Test
    @DisplayName("Deve lançar exceção para reemissão além do limite")
    void deveLancarErroParaReemissaoAlemDoLimite() {
        boletoValido.setStatus(BoletoStatus.EMITIDO);
        boletoValido.setNumeroReemissoes(3); // Limite máximo
        assertThrows(BoletoValidationException.class, () -> validator.validarReemissao(boletoValido));
    }

    // Testes para validarCancelamento
    @ParameterizedTest
    @EnumSource(value = BoletoStatus.class, names = {"EMITIDO", "REEMITIDO", "VENCIDO"})
    @DisplayName("Deve aceitar cancelamento para estados permitidos")
    void deveAceitarCancelamentoParaEstadosPermitidos(BoletoStatus status) {
        boletoValido.setStatus(status);
        assertDoesNotThrow(() -> validator.validarCancelamento(boletoValido));
    }

    @ParameterizedTest
    @EnumSource(value = BoletoStatus.class, names = {"PAGO", "CANCELADO"})
    @DisplayName("Deve lançar exceção para cancelamento em estados não permitidos")
    void deveLancarErroParaCancelamentoEmEstadosNaoPermitidos(BoletoStatus status) {
        boletoValido.setStatus(status);
        assertThrows(BoletoValidationException.class, () -> validator.validarCancelamento(boletoValido));
    }

    // Testes para validarPagamento
    @ParameterizedTest
    @EnumSource(value = BoletoStatus.class, names = {"EMITIDO", "REEMITIDO", "VENCIDO"})
    @DisplayName("Deve aceitar pagamento para estados permitidos e valor suficiente")
    void deveAceitarPagamentoParaEstadosPermitidosEValorSuficiente(BoletoStatus status) {
        boletoValido.setStatus(status);
        boletoValido.setValor(new BigDecimal("100.00"));
        BigDecimal valorPago = new BigDecimal("100.00");
        assertDoesNotThrow(() -> validator.validarPagamento(boletoValido, valorPago));
    }

    @ParameterizedTest
    @EnumSource(value = BoletoStatus.class, names = {"PAGO", "CANCELADO"})
    @DisplayName("Deve lançar exceção para pagamento em estados não permitidos")
    void deveLancarErroParaPagamentoEmEstadosNaoPermitidos(BoletoStatus status) {
        boletoValido.setStatus(status);
        boletoValido.setValor(new BigDecimal("100.00"));
        BigDecimal valorPago = new BigDecimal("100.00");
        assertThrows(BoletoValidationException.class, () -> validator.validarPagamento(boletoValido, valorPago));
    }

    @Test
    @DisplayName("Deve lançar exceção para pagamento com valor insuficiente")
    void deveLancarErroParaPagamentoComValorInsuficiente() {
        boletoValido.setStatus(BoletoStatus.EMITIDO);
        boletoValido.setValor(new BigDecimal("100.00"));
        BigDecimal valorPago = new BigDecimal("99.99");
        assertThrows(BoletoValidationException.class, () -> validator.validarPagamento(boletoValido, valorPago));
    }

    @Test
    @DisplayName("Deve lançar exceção para pagamento com valor nulo")
    void deveLancarErroParaPagamentoComValorNulo() {
        boletoValido.setStatus(BoletoStatus.EMITIDO);
        boletoValido.setValor(new BigDecimal("100.00"));
        assertThrows(BoletoValidationException.class, () -> validator.validarPagamento(boletoValido, null));
    }

    // Teste removido: validarCancelamento com motivo
    // Motivo: A validação atual não inclui verificação de motivo
}
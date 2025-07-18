package com.pagamento.boleto.domain.service;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.DadosTecnicosBoleto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Cálculos de Boleto")
class BoletoCalculosTest {

    @InjectMocks
    private BoletoCalculos calculos;

    private Boleto boleto;

    @BeforeEach
    void setup() {
        boleto = new Boleto();
        boleto.setId("BOL-12345");
        boleto.setValor(new BigDecimal("1000.00"));
        boleto.setDataVencimento(LocalDate.of(2025, 12, 31));
        boleto.setBeneficiario("Empresa Beneficiária Ltda.");
    }

    @Test
    @DisplayName("Deve gerar dados técnicos completos")
    void deveGerarDadosTecnicosCompletos() {
        // Act
        DadosTecnicosBoleto dados = calculos.gerarDadosTecnicos(boleto);
        
        // Assert
        assertNotNull(dados);
        assertNotNull(dados.codigoBarras());
        assertNotNull(dados.linhaDigitavel());
        assertNotNull(dados.qrCode());
        assertNotNull(dados.nossoNumero());
        
        // Verificar estrutura básica
        assertEquals(44, dados.codigoBarras().length());
        assertTrue(dados.linhaDigitavel().matches("^\\d{5}\\.\\d{5} \\d{5}\\.\\d{6} \\d{5}\\.\\d{6} \\d \\d{14}$"));
        assertTrue(dados.qrCode().matches("000201.*6304[0-9A-F]{4}$"));
        assertTrue(dados.nossoNumero().matches("^\\d+-\\d{4}$"));
    }

    @ParameterizedTest
    @CsvSource({
        "2023-01-01, 9225",   // Data aleatória
        "1997-10-07, 0000",   // Data base
        "1997-10-08, 0001",   // Dia seguinte
        "2025-12-31, 10318",  // Data futura
        "2078-12-31, 29626"   // Data limite (máximo 9999 dias)
    })
    @DisplayName("Deve calcular fator de vencimento corretamente")
    void deveCalcularFatorVencimento(String dataStr, String expected) {
        // Arrange
        LocalDate data = LocalDate.parse(dataStr);
        
        // Act
        String fator = calculos.calcularFatorVencimento(data);
        
        // Assert
        assertEquals(expected, fator);
        assertEquals(4, fator.length());
        assertTrue(fator.matches("\\d{4}"));
    }

    @Test
    @DisplayName("Deve lançar exceção para data de vencimento nula")
    void deveLancarExcecaoParaDataVencimentoNula() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> calculos.calcularFatorVencimento(null));
    }

    @ParameterizedTest
    @CsvSource({
        "1000.00, 100000",
        "123.45, 12345",
        "0.99, 99",
        "999999.99, 99999999",
        "0.01, 1",
        "9999999.99, 999999999"  // Valor máximo
    })
    @DisplayName("Deve formatar valor do boleto corretamente")
    void deveFormatarValorBoleto(String valorStr, String expected) {
        // Arrange
        BigDecimal valor = new BigDecimal(valorStr);
        
        // Act
        String resultado = calculos.formatarValorBoleto(valor);
        
        // Assert
        assertEquals(expected, resultado);
        assertFalse(resultado.contains("."));
    }

    @Test
    @DisplayName("Deve lançar exceção para valor nulo")
    void deveLancarExcecaoParaValorNulo() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> calculos.formatarValorBoleto(null));
    }

    @Test
    @DisplayName("Deve gerar código de barras válido")
    void deveGerarCodigoBarrasValido() {
        // Act
        String codigo = calculos.gerarCodigoBarras(boleto);
        
        // Assert
        assertNotNull(codigo);
        assertEquals(44, codigo.length());
        assertTrue(codigo.matches("^\\d{44}$"));
        assertTrue(codigo.startsWith("3419")); // Banco + moeda
        assertTrue(codigo.contains("10318")); // Fator vencimento (31/12/2025)
        assertTrue(codigo.endsWith("100000")); // Valor formatado
    }

    @Test
    @DisplayName("Deve gerar linha digitável no padrão FEBRABAN")
    void deveGerarLinhaDigitavelNoPadraoFebraban() {
        // Arrange
        String codigoBarras = "34191031800001000001123456789012345678901234";
        
        // Act
        String linha = calculos.gerarLinhaDigitavel(codigoBarras);
        
        // Assert
        assertNotNull(linha);
        assertEquals(54, linha.length());
        assertTrue(linha.matches("^\\d{5}\\.\\d{5} \\d{5}\\.\\d{6} \\d{5}\\.\\d{6} \\d \\d{14}$"));
    }

    @Test
    @DisplayName("Deve gerar QR Code válido com CRC calculado")
    void deveGerarQRCodeValidoComCRC() {
        // Act
        String qrCode = calculos.gerarQRCode(boleto);
        
        // Assert
        assertNotNull(qrCode);
        assertTrue(qrCode.startsWith("000201"));
        assertTrue(qrCode.contains("BR.GOV.BCB.PIX"));
        assertTrue(qrCode.contains("BOL-12345")); // ID do boleto
        assertTrue(qrCode.contains("5303986")); // Código moeda
        assertTrue(qrCode.contains("5802BR"));
        assertTrue(qrCode.contains("6008BRASILIA"));
        assertTrue(qrCode.matches(".*6304[0-9A-F]{4}$")); // CRC calculado
        assertTrue(qrCode.contains("Empresa Benef")); // Beneficiário limitado
    }

    @Test
    @DisplayName("Deve limitar beneficiário no QR Code para 13 caracteres")
    void deveLimitarBeneficiarioQRCode() {
        // Arrange
        boleto.setBeneficiario("Nome Muito Longo Que Deve Ser Truncado Para Caber no Campo");
        
        // Act
        String qrCode = calculos.gerarQRCode(boleto);
        int index = qrCode.indexOf("5913");
        assertTrue(index > 0, "Deve conter o campo do beneficiário");
        String beneficiarioPart = qrCode.substring(index + 4, index + 4 + 13);
        
        // Assert
        assertEquals("Nome Muito Lo", beneficiarioPart);
        assertEquals(13, beneficiarioPart.length());
    }

    @Test
    @DisplayName("Deve gerar nosso número com timestamp e random")
    void deveGerarNossoNumeroComTimestampERandom() {
        try (MockedStatic<System> systemMock = mockStatic(System.class);
             MockedStatic<ThreadLocalRandom> randomMock = mockStatic(ThreadLocalRandom.class)) {
            
            // Arrange
            systemMock.when(System::currentTimeMillis).thenReturn(1700000000000L);
            randomMock.when(() -> ThreadLocalRandom.current().nextInt(1000, 9999)).thenReturn(5678);
            
            // Act
            String nossoNumero = calculos.gerarNossoNumero();
            
            // Assert
            assertEquals("1700000000000-5678", nossoNumero);
        }
    }

    @Test
    @DisplayName("Deve gerar nosso número com formato correto")
    void deveGerarNossoNumeroFormatoCorreto() {
        // Act
        String nossoNumero = calculos.gerarNossoNumero();
        
        // Assert
        assertNotNull(nossoNumero);
        assertTrue(nossoNumero.matches("^\\d+-\\d{4}$"));
        String[] partes = nossoNumero.split("-");
        assertEquals(2, partes.length);
        assertTrue(partes[0].length() >= 10);
        assertEquals(4, partes[1].length());
    }

    @ParameterizedTest
    @CsvSource({
        "abc, 5, abc",
        "abcde, 5, abcde",
        "abcdefg, 5, abcde",
        "'', 5, ''",
        "abc, 0, ''"
    })
    @DisplayName("Deve limitar string corretamente")
    void deveLimitarStringCorretamente(String input, int limite, String expected) {
        // Act & Assert
        assertEquals(expected, calculos.limitarString(input, limite));
    }

    @Test
    @DisplayName("Deve retornar nulo para string nula")
    void deveRetornarNuloParaStringNula() {
        assertNull(calculos.limitarString(null, 5));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "12345", "PIX", "00020126580014BR.GOV.BCB.PIX"})
    @DisplayName("Deve calcular CRC16 corretamente para diferentes payloads")
    void deveCalcularCRC16Corretamente(String payload) {
        // Act
        String crc = calculos.calcularCRC16(payload);
        
        // Assert
        assertNotNull(crc);
        assertEquals(4, crc.length());
        assertTrue(crc.matches("[0-9A-F]{4}"));
    }

    @Test
    @DisplayName("Deve calcular CRC16 conhecido para payload específico")
    void deveCalcularCRC16Conhecido() {
        // Arrange
        String payload = "00020126580014BR.GOV.BCB.PIX";
        
        // Act
        String crc = calculos.calcularCRC16(payload);
        
        // Assert
        assertEquals("1D0F", crc); // Valor conhecido para este payload
    }
}
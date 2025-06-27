package com.pagamento.boleto.domain.model;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.service.BoletoCalculos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Fábrica de Boletos")
class BoletoFactoryTest {

    @Mock
    private BoletoCalculos calculos;
    
    private BoletoFactory factory;
    private DadosTecnicosBoleto dadosTecnicosMock;

    @BeforeEach
    void setup() {
        factory = new BoletoFactory(calculos);
        dadosTecnicosMock = new DadosTecnicosBoleto(
            "3419334470000123456789012345678901234567890",
            "34191.23457 89012.345678 90123.456789 3 44700001234567",
            "00020126580014BR.GOV.BCB.PIX0136123...",
            "2024000001"
        );
        when(calculos.gerarDadosTecnicos(any())).thenReturn(dadosTecnicosMock);
    }

    @Test
    @DisplayName("Deve criar novo boleto com dados completos")
    void deveCriarNovoBoletoComDadosCompletos() {
        // Arrange
        LocalDate dataEmissao = LocalDate.of(2023, 6, 15);
        LocalDate dataVencimento = LocalDate.of(2023, 7, 15);
        
        BoletoRequestDTO dto = new BoletoRequestDTO(
            "Cliente A", "Beneficiário B", 1000.50, 
            dataVencimento, "DOC-123", "Pagar até vencimento", "Banco XYZ"
        );

        // Act
        Boleto boleto = factory.criarBoleto(dto);

        // Assert
        assertNotNull(boleto);
        assertEquals("Cliente A", boleto.getPagador());
        assertEquals("Beneficiário B", boleto.getBeneficiario());
        assertEquals(new BigDecimal("1000.50"), boleto.getValor());
        assertEquals(dataVencimento, boleto.getDataVencimento());
        assertEquals(dataEmissao, boleto.getDataEmissao()); // Não deve usar default
        assertEquals("DOC-123", boleto.getDocumento());
        assertEquals("Pagar até vencimento", boleto.getInstrucoes());
        assertEquals("Banco XYZ", boleto.getLocalPagamento());
        assertEquals(BoletoStatus.EMITIDO, boleto.getStatus());
        assertEquals(dadosTecnicosMock, boleto.getDadosTecnicos());
        assertNull(boleto.getBoletoOriginalId());
        assertEquals(0, boleto.getReemissoes());
    }

    @Test
    @DisplayName("Deve usar valores default para campos opcionais")
    void deveUsarValoresDefaultParaCamposOpcionais() {
        // Arrange
        BoletoRequestDTO dto = new BoletoRequestDTO(
            "Cliente A", "Beneficiário B", 500.0, 
            LocalDate.now().plusDays(30), null, null, null
        );

        // Act
        Boleto boleto = factory.criarBoleto(dto);

        // Assert
        assertEquals(LocalDate.now(), boleto.getDataEmissao());
        assertEquals("", boleto.getDocumento());
        assertEquals("", boleto.getInstrucoes());
        assertEquals("Pagável em qualquer banco", boleto.getLocalPagamento());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Deve lançar exceção para pagador inválido")
    void deveLancarExcecaoParaPagadorInvalido(String pagador) {
        BoletoRequestDTO dto = new BoletoRequestDTO(
            pagador, "Beneficiário", 100.0, 
            LocalDate.now().plusDays(1), "DOC", "Inst", "Local"
        );
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> factory.criarBoleto(dto));
        
        assertEquals("Pagador é obrigatório", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Deve lançar exceção para beneficiário inválido")
    void deveLancarExcecaoParaBeneficiarioInvalido(String beneficiario) {
        BoletoRequestDTO dto = new BoletoRequestDTO(
            "Pagador", beneficiario, 100.0, 
            LocalDate.now().plusDays(1), "DOC", "Inst", "Local"
        );
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> factory.criarBoleto(dto));
        
        assertEquals("Beneficiário é obrigatório", ex.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
        "0.0",
        "-100.0",
        "0"
    })
    @DisplayName("Deve lançar exceção para valor inválido")
    void deveLancarExcecaoParaValorInvalido(double valor) {
        BoletoRequestDTO dto = new BoletoRequestDTO(
            "Pagador", "Beneficiário", valor, 
            LocalDate.now().plusDays(1), "DOC", "Inst", "Local"
        );
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> factory.criarBoleto(dto));
        
        assertEquals("Valor do boleto inválido", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para data de vencimento nula")
    void deveLancarExcecaoParaDataVencimentoNula() {
        BoletoRequestDTO dto = new BoletoRequestDTO(
            "Pagador", "Beneficiário", 100.0, 
            null, "DOC", "Inst", "Local"
        );
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> factory.criarBoleto(dto));
        
        assertEquals("Data de vencimento obrigatória", ex.getMessage());
    }

    @Test
    @DisplayName("Deve criar reemissão corretamente")
    void deveCriarReemissaoCorretamente() {
        // Arrange
        Boleto original = new Boleto();
        original.setId(UUID.randomUUID().toString());
        original.setPagador("Cliente Original");
        original.setBeneficiario("Beneficiário Original");
        original.setValor(new BigDecimal("200.00"));
        original.setDocumento("DOC-ORIG");
        original.setInstrucoes("Instruções Originais");
        original.setLocalPagamento("Banco Original");
        original.setDataVencimento(LocalDate.of(2023, 6, 30));
        original.setReemissoes(1);

        int diasAdicionais = 45;
        
        // Act
        Boleto reemissao = factory.criarReemissao(original, diasAdicionais);

        // Assert
        assertNotNull(reemissao);
        assertEquals(original.getPagador(), reemissao.getPagador());
        assertEquals(original.getBeneficiario(), reemissao.getBeneficiario());
        assertEquals(original.getValor(), reemissao.getValor());
        assertEquals(original.getDocumento(), reemissao.getDocumento());
        assertEquals(original.getInstrucoes(), reemissao.getInstrucoes());
        assertEquals(original.getLocalPagamento(), reemissao.getLocalPagamento());
        assertEquals(LocalDate.now(), reemissao.getDataEmissao());
        assertEquals(original.getDataVencimento().plusDays(diasAdicionais), reemissao.getDataVencimento());
        assertEquals(original.getId(), reemissao.getBoletoOriginalId());
        assertEquals(BoletoStatus.REEMITIDO, reemissao.getStatus());
        assertEquals(dadosTecnicosMock, reemissao.getDadosTecnicos());
        assertNotEquals(original.getId(), reemissao.getId());
        assertEquals(0, reemissao.getReemissoes());
    }

    @Test
    @DisplayName("Deve gerar novos dados técnicos na reemissão")
    void deveGerarNovosDadosTecnicosNaReemissao() {
        // Arrange
        Boleto original = new Boleto();
        original.setId("BOL-ORIG");
        
        // Criar novos dados técnicos diferentes
        DadosTecnicosBoleto novosDados = new DadosTecnicosBoleto(
            "NOVO-COD-BARRAS",
            "NOVA-LINHA-DIG",
            "NOVO-QR-CODE",
            "NOVO-NOSSO-NUM"
        );
        
        when(calculos.gerarDadosTecnicos(any())).thenReturn(novosDados);

        // Act
        Boleto reemissao = factory.criarReemissao(original, 30);

        // Assert
        assertEquals(novosDados, reemissao.getDadosTecnicos());
        assertNotEquals(original.getDadosTecnicos(), reemissao.getDadosTecnicos());
    }

    @Test
    @DisplayName("Deve lançar exceção para reemissão com original nulo")
    void deveLancarExcecaoParaReemissaoComOriginalNulo() {
        assertThrows(NullPointerException.class, 
            () -> factory.criarReemissao(null, 30));
    }

    @Test
    @DisplayName("Deve lançar exceção para dias adicionais negativos")
    void deveLancarExcecaoParaDiasAdicionaisNegativos() {
        Boleto original = new Boleto();
        assertThrows(IllegalArgumentException.class, 
            () -> factory.criarReemissao(original, -1));
    }

    @Test
    @DisplayName("Deve gerar ID diferente na reemissão")
    void deveGerarIdDiferenteNaReemissao() {
        // Arrange
        Boleto original = new Boleto();
        original.setId("ID-ORIGINAL");
        
        // Act
        Boleto reemissao = factory.criarReemissao(original, 30);
        
        // Assert
        assertNotNull(reemissao.getId());
        assertNotEquals(original.getId(), reemissao.getId());
        assertFalse(reemissao.getId().isEmpty());
    }

    @Test
    @DisplayName("Deve manter dados financeiros na reemissão")
    void deveManterDadosFinanceirosNaReemissao() {
        // Arrange
        Boleto original = new Boleto();
        original.setValor(new BigDecimal("350.99"));
        original.setDescontos(new BigDecimal("10.00"));
        original.setMulta(new BigDecimal("5.00"));
        
        // Act
        Boleto reemissao = factory.criarReemissao(original, 30);
        
        // Assert
        assertEquals(original.getValor(), reemissao.getValor());
        assertEquals(original.getDescontos(), reemissao.getDescontos());
        assertEquals(original.getMulta(), reemissao.getMulta());
    }
}
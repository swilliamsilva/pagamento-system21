package com.pagamento.boleto;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.exception.*;
import com.pagamento.boleto.domain.model.*;
import com.pagamento.boleto.domain.ports.*;
import com.pagamento.boleto.domain.service.*;
import com.pagamento.common.dto.BoletoRequestDTO as CommonBoletoRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("Testes do Serviço de Boletos")
class BoletoServiceTest {

    @Mock private BoletoRepositoryPort repository;
    @Mock private AsaasGatewayPort asaasGateway;
    @Mock private NotificacaoPort notificacaoPort;
    @Mock private BoletoValidation validation;
    @Mock private BoletoFactory factory;
    @Mock private TaxasService taxasService;
    @Mock private PdfService pdfService;
    @Mock private BoletoCalculos calculos;

    @InjectMocks
    private BoletoService service;

    private Boleto boletoSimulado;
    private DadosTecnicosBoleto dadosTecnicosSimulado;
    private final String ID_VALIDO = UUID.randomUUID().toString();
    private final String ID_EXTERNO_VALIDO = "ASAAS_123";

    @BeforeEach
    void setup() {
        // Configurar dados técnicos simulados
        dadosTecnicosSimulado = new DadosTecnicosBoleto(
            "3419334470000123456789012345678901234567890",
            "34191.23457 89012.345678 90123.456789 3 44700001234567",
            "00020126580014BR.GOV.BCB.PIX0136123...",
            "2024000001"
        );
        
        // Configurar boleto simulado
        boletoSimulado = new Boleto();
        boletoSimulado.setId(ID_VALIDO);
        boletoSimulado.setValor(new BigDecimal("200.00"));
        boletoSimulado.setStatus(BoletoStatus.EMITIDO);
        boletoSimulado.setIdExterno(ID_EXTERNO_VALIDO);
        boletoSimulado.setDadosTecnicos(dadosTecnicosSimulado);
    }

    // Teste de Emissão
    @Test
    @DisplayName("Deve emitir boleto com sucesso")
    void deveEmitirBoletoComSucesso() {
        // Arrange
        CommonBoletoRequestDTO request = new CommonBoletoRequestDTO(
            "Cliente A", "Beneficiário B", BigDecimal.valueOf(200.00), 
            LocalDate.of(2025, 7, 1), "DOC123", "Pagar até vencimento", "Qualquer banco"
        );
        
        when(factory.criarBoleto(any())).thenReturn(boletoSimulado);
        when(repository.salvar(any())).thenReturn(boletoSimulado);
        when(asaasGateway.registrarBoleto(any())).thenReturn(ID_EXTERNO_VALIDO);

        // Act
        String idBoleto = service.gerarBoleto(request);

        // Assert
        assertNotNull(idBoleto);
        assertEquals(ID_VALIDO, idBoleto);
        
        // Verificar interações
        verify(validation).validarEmissao(any());
        verify(taxasService).aplicarTaxasEmissao(any());
        verify(validation).validarBoleto(any());
        verify(repository, times(2)).salvar(any());
        verify(asaasGateway).registrarBoleto(any());
        verify(notificacaoPort).notificarEmissao(any());
    }

    // Teste de Código de Barras
    @Test
    @DisplayName("Deve gerar código de barras válido")
    void deveGerarCodigoBarrasValido() {
        // Arrange
        BoletoRequestDTO request = new BoletoRequestDTO(
            "Cliente A", "Beneficiário B", 200.0, 
            LocalDate.of(2025, 7, 1), "DOC123", "Instruções", "Local"
        );
        
        when(factory.criarBoleto(any())).thenReturn(boletoSimulado);
        when(calculos.gerarDadosTecnicos(any())).thenReturn(dadosTecnicosSimulado);

        // Act
        Boleto boleto = service.emitirBoleto(request);

        // Assert
        assertNotNull(boleto.getDadosTecnicos());
        assertNotNull(boleto.getDadosTecnicos().codigoBarras());
        assertEquals(44, boleto.getDadosTecnicos().codigoBarras().length());
        assertTrue(boleto.getDadosTecnicos().codigoBarras().matches("^[0-9]{44}$"));
    }

    // Teste de Validação
    @Test
    @DisplayName("Deve lançar exceção quando validação falhar")
    void deveLancarExcecaoQuandoValidacaoFalhar() {
        // Arrange
        CommonBoletoRequestDTO request = new CommonBoletoRequestDTO(
            null, null, BigDecimal.valueOf(-100), 
            LocalDate.now().minusDays(1), null, null, null
        );
        
        doThrow(new BoletoValidationException("Valor inválido"))
            .when(validation).validarEmissao(any());

        // Act & Assert
        assertThrows(BoletoValidationException.class, () -> service.gerarBoleto(request));
    }

    // Teste de Rollback
    @Test
    @DisplayName("Deve fazer rollback quando gateway falhar")
    void deveFazerRollbackQuandoGatewayFalhar() {
        // Arrange
        CommonBoletoRequestDTO request = new CommonBoletoRequestDTO(
            "Cliente", "Beneficiário", BigDecimal.valueOf(100), 
            LocalDate.now().plusDays(10), "DOC", "Inst", "Local"
        );
        
        when(factory.criarBoleto(any())).thenReturn(boletoSimulado);
        when(repository.salvar(any())).thenReturn(boletoSimulado);
        when(asaasGateway.registrarBoleto(any()))
            .thenThrow(new GatewayIntegrationException("Falha no gateway"));

        // Act & Assert
        assertThrows(GatewayIntegrationException.class, () -> service.gerarBoleto(request));
        
        // Verificar rollback
        verify(repository).deletarPorId(ID_VALIDO);
        verify(notificacaoPort, never()).notificarEmissao(any());
    }

    // Teste de Reemissão
    @Test
    @DisplayName("Deve reemitir boleto com novos dados técnicos")
    void deveReemitirBoletoComNovosDadosTecnicos() {
        // Arrange
        DadosTecnicosBoleto novosDados = new DadosTecnicosBoleto(
            "3419334470000987654321098765432109876543210",
            "34191.23457 89012.345678 90123.456789 3 44700009876543",
            "00020126580014BR.GOV.BCB.PIX0136987...",
            "2024000002"
        );
        
        Boleto reemissaoSimulada = new Boleto();
        reemissaoSimulada.setId(UUID.randomUUID().toString());
        reemissaoSimulada.setDadosTecnicos(novosDados);
        
        when(repository.buscarPorId(ID_VALIDO)).thenReturn(Optional.of(boletoSimulado));
        when(factory.criarReemissao(eq(boletoSimulado), anyInt())).thenReturn(reemissaoSimulada);
        when(repository.salvar(any())).thenReturn(reemissaoSimulada);
        when(asaasGateway.registrarBoleto(any())).thenReturn("ASAAS_NEW_123");

        // Act
        Boleto reemissao = service.reemitirBoleto(ID_VALIDO);

        // Assert
        assertNotNull(reemissao);
        assertNotEquals(boletoSimulado.getId(), reemissao.getId());
        assertEquals(novosDados, reemissao.getDadosTecnicos());
        
        // Verificar atualização do original
        verify(repository).atualizar(boletoSimulado);
        assertEquals(1, boletoSimulado.getReemissoes());
        
        // Verificar notificação
        verify(notificacaoPort).notificarReemissao(eq(boletoSimulado), eq(reemissao));
    }

    // Teste de Cancelamento
    @Test
    @DisplayName("Deve cancelar boleto e marcar status corretamente")
    void deveCancelarBoletoEMarcarStatus() {
        // Arrange
        String motivo = "Cancelamento solicitado pelo cliente";
        when(repository.buscarPorId(ID_VALIDO)).thenReturn(Optional.of(boletoSimulado));
        
        // Act
        Boleto cancelado = service.cancelarBoleto(ID_VALIDO, motivo);

        // Assert
        assertEquals(BoletoStatus.CANCELADO, cancelado.getStatus());
        assertEquals(motivo, cancelado.getMotivoCancelamento());
        
        // Verificar operações externas
        verify(asaasGateway).cancelarBoleto(ID_EXTERNO_VALIDO);
        verify(notificacaoPort).notificarCancelamento(cancelado);
    }

    // Teste de Geração de PDF
    @Test
    @DisplayName("Deve gerar PDF com conteúdo válido")
    void deveGerarPdfComConteudoValido() {
        // Arrange
        byte[] pdfSimulado = "%PDF-1.4 fake content %%EOF".getBytes();
        when(repository.buscarPorId(ID_VALIDO)).thenReturn(Optional.of(boletoSimulado));
        when(pdfService.gerarPdf(any())).thenReturn(pdfSimulado);
        
        // Act
        byte[] pdf = service.gerarPDF(ID_VALIDO);

        // Assert
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
        
        // Verificar header do PDF: %PDF
        String header = new String(pdf, 0, 5);
        assertEquals("%PDF-", header);
    }

    // Teste de Consulta
    @Test
    @DisplayName("Deve consultar boleto por ID")
    void deveConsultarBoletoPorId() {
        // Arrange
        when(repository.buscarPorId(ID_VALIDO)).thenReturn(Optional.of(boletoSimulado));
        
        // Act
        Boleto encontrado = service.consultarBoleto(ID_VALIDO);
        
        // Assert
        assertNotNull(encontrado);
        assertEquals(ID_VALIDO, encontrado.getId());
        assertEquals(new BigDecimal("200.00"), encontrado.getValor());
    }

    // Teste de Boleto Não Encontrado
    @Test
    @DisplayName("Deve lançar exceção quando boleto não encontrado")
    void deveLancarExcecaoQuandoBoletoNaoEncontrado() {
        // Arrange
        String idInexistente = "ID_INEXISTENTE";
        when(repository.buscarPorId(idInexistente)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(BoletoNotFoundException.class, () -> service.consultarBoleto(idInexistente));
        assertThrows(BoletoNotFoundException.class, () -> service.gerarPDF(idInexistente));
        assertThrows(BoletoNotFoundException.class, () -> service.cancelarBoleto(idInexistente, "Motivo"));
    }

    // Teste de Reemissão Inválida
    @Test
    @DisplayName("Deve lançar exceção ao reemitir boleto com status inválido")
    void deveLancarExcecaoAoReemitirBoletoComStatusInvalido() {
        // Arrange
        boletoSimulado.setStatus(BoletoStatus.PAGO); // Status não permitido
        when(repository.buscarPorId(ID_VALIDO)).thenReturn(Optional.of(boletoSimulado));
        
        // Act & Assert
        assertThrows(BoletoValidationException.class, () -> service.reemitirBoleto(ID_VALIDO));
    }

    // Teste de Cancelamento Inválido
    @Test
    @DisplayName("Deve lançar exceção ao cancelar boleto já pago")
    void deveLancarExcecaoAoCancelarBoletoJaPago() {
        // Arrange
        boletoSimulado.setStatus(BoletoStatus.PAGO);
        when(repository.buscarPorId(ID_VALIDO)).thenReturn(Optional.of(boletoSimulado));
        
        // Act & Assert
        assertThrows(BoletoValidationException.class, () -> 
            service.cancelarBoleto(ID_VALIDO, "Motivo")
        );
    }

    // Teste de Geração de QR Code
    @Test
    @DisplayName("Deve gerar QR Code válido")
    void deveGerarQrCodeValido() {
        // Arrange
        when(repository.buscarPorId(ID_VALIDO)).thenReturn(Optional.of(boletoSimulado));
        
        // Act
        String qrCode = service.gerarQRCode(ID_VALIDO);
        
        // Assert
        assertNotNull(qrCode);
        assertTrue(qrCode.startsWith("000201"));
        assertTrue(qrCode.contains("BR.GOV.BCB.PIX"));
        assertTrue(qrCode.length() > 50);
    }
}
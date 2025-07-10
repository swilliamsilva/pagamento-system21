package com.pagamento.boleto.domain.service;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.exception.*;
import com.pagamento.boleto.domain.model.*;
import com.pagamento.boleto.domain.ports.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoletoServiceTest {

    @Mock
    private BoletoRepositoryPort repository;
    
    @Mock
    private AsaasGatewayPort asaasGateway;
    
    @Mock
    private NotificacaoPort notificacaoPort;
    
    @Mock
    private BoletoValidation validation;
    
    @Mock
    private BoletoFactory factory;
    
    @Mock
    private TaxasService taxasService;
    
    @Mock
    private PdfService pdfService;
    
    @Mock
    private ApplicationContext applicationContext;
    
    @Mock
    private BoletoServicePort transactionalProxy;
    
    @InjectMocks
    private BoletoService boletoService;

    private BoletoRequestDTO requestDTO;
    private Boleto boleto;

    @BeforeEach
    void setUp() {
        requestDTO = new BoletoRequestDTO(
            "Cliente A",
            "Beneficiário B",
            BigDecimal.valueOf(1000.00),
            LocalDate.now().plusDays(30),
            "DOC-123",
            "Instruções",
            "Local de pagamento"
        );
        
        boleto = new Boleto();
        boleto.setId("boleto-123");
        
        // Configurar proxy transacional
        when(applicationContext.getBean(BoletoServicePort.class)).thenReturn(transactionalProxy);
        when(transactionalProxy.consultarBoleto(any())).thenReturn(boleto);
        when(transactionalProxy.emitirBoleto(any())).thenReturn(boleto);
    }

    @Test
    void emitirBoleto_ShouldWorkSuccessfully() {
        // Arrange
        when(factory.criarBoleto(any())).thenReturn(boleto);
        when(repository.salvar(any())).thenReturn(boleto);
        when(asaasGateway.registrarBoleto(any())).thenReturn("asaas-123");

        // Act
        Boleto result = boletoService.emitirBoleto(requestDTO);

        // Assert
        assertNotNull(result);
        verify(validation).validarEmissao(requestDTO);
        verify(taxasService).aplicarTaxasEmissao(boleto);
        verify(validation).validarBoleto(boleto);
        verify(notificacaoPort).notificarEmissao(boleto);
    }

    @Test
    void emitirBoleto_ShouldDeleteWhenGatewayFails() {
        // Arrange
        when(factory.criarBoleto(any())).thenReturn(boleto);
        when(repository.salvar(any())).thenReturn(boleto);
        when(asaasGateway.registrarBoleto(any())).thenThrow(new GatewayIntegrationException("Erro"));

        // Act & Assert
        assertThrows(GatewayIntegrationException.class, () -> 
            boletoService.emitirBoleto(requestDTO)
        );
        verify(repository).deletar(boleto.getId());
    }

    @Test
    void consultarBoleto_ShouldReturnBoleto() {
        // Arrange
        when(repository.buscarPorId("123")).thenReturn(Optional.of(boleto));

        // Act
        Boleto result = boletoService.consultarBoleto("123");

        // Assert
        assertEquals(boleto, result);
    }

    @Test
    void consultarBoleto_ShouldThrowWhenNotFound() {
        // Arrange
        when(repository.buscarPorId("123")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BoletoNotFoundException.class, () -> 
            boletoService.consultarBoleto("123")
        );
    }

    @Test
    void gerarPDF_ShouldUseTransactionalProxy() {
        // Arrange
        when(pdfService.gerarPdf(any())).thenReturn(new byte[0]);

        // Act
        byte[] result = boletoService.gerarPDF("123");

        // Assert
        assertNotNull(result);
        verify(transactionalProxy).consultarBoleto("123");
    }

    @Test
    void cancelarBoleto_ShouldWorkSuccessfully() {
        // Arrange
        when(repository.buscarPorId("123")).thenReturn(Optional.of(boleto));

        // Act
        Boleto result = boletoService.cancelarBoleto("123", "Motivo");

        // Assert
        assertNotNull(result);
        verify(validation).validarCancelamento(boleto);
        verify(notificacaoPort).notificarCancelamento(boleto);
        assertEquals(BoletoStatus.CANCELADO, boleto.getStatus());
    }

    @Test
    void deprecatedCancelarBoleto_ShouldUseTransactionalProxy() {
        // Act
        boletoService.cancelarBoleto("123");

        // Assert
        verify(transactionalProxy).cancelarBoleto("123", "Cancelamento solicitado");
    }

    @Test
    void gerarBoleto_ShouldUseTransactionalProxy() {
        // Arrange
        com.pagamento.common.dto.BoletoRequestDTO commonRequest = 
            new com.pagamento.common.dto.BoletoRequestDTO(
                "Cliente", "Benef", BigDecimal.TEN, LocalDate.now(), 
                "DOC", "Inst", "Local"
            );

        // Act
        String result = boletoService.gerarBoleto(commonRequest);

        // Assert
        assertEquals(boleto.getId(), result);
        verify(transactionalProxy).emitirBoleto(any());
    }
}
package com.pagamento.pix.service;

import com.pagamento.pix.core.ports.out.BacenPort;
import com.pagamento.pix.core.ports.out.PixRepositoryPort;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.domain.service.PixValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PixServiceTest {

    @Mock
    private BacenPort bacenPort;

    @Mock
    private PixRepositoryPort pixRepositoryPort;

    @Mock
    private PixValidator pixValidator;

    @InjectMocks
    private PixService pixService;

    private Pix pix;

    @BeforeEach
    void setUp() {
        pix = new Pix();
        pix.setId("PIX-123");
        pix.setValor(new BigDecimal("100.00"));
        pix.setDataTransacao(LocalDateTime.now());
        // Outras propriedades necessárias
    }

    @Test
    void deveProcessarPixComSucesso() {
        // Configurar mocks
        when(pixValidator.validar(any(Pix.class))).thenReturn(true);
        when(pixRepositoryPort.salvar(any(Pix.class))).thenReturn(pix);
        when(bacenPort.enviarTransacao(any(Pix.class))).thenReturn("BID-12345");
        
        // Executar
        Pix resultado = pixService.processarPix(pix);
        
        // Verificar
        assertNotNull(resultado);
        assertEquals("PROCESSADO", resultado.getStatus());
        assertEquals("BID-12345", resultado.getBacenId());
        
        // Verificar interações
        verify(pixValidator, times(1)).validar(pix);
        verify(pixRepositoryPort, times(2)).salvar(any(Pix.class)); // Salva inicial + atualizado
        verify(bacenPort, times(1)).enviarTransacao(pix);
    }

    @Test
    void deveRejeitarPixInvalido() {
        // Configurar mock
        when(pixValidator.validar(any(Pix.class))).thenReturn(false);
        
        // Executar
        Pix resultado = pixService.processarPix(pix);
        
        // Verificar
        assertNotNull(resultado);
        assertEquals("REJEITADO", resultado.getStatus());
        assertNull(resultado.getBacenId());
        
        // Verificar interações
        verify(pixValidator, times(1)).validar(pix);
        verify(pixRepositoryPort, times(1)).salvar(pix); // Apenas salva o status inicial
        verify(bacenPort, never()).enviarTransacao(any());
    }

    @Test
    void deveLidarComErroNoBacen() {
        // Configurar mocks
        when(pixValidator.validar(any(Pix.class))).thenReturn(true);
        when(pixRepositoryPort.salvar(any(Pix.class))).thenReturn(pix);
        when(bacenPort.enviarTransacao(any(Pix.class))).thenThrow(new RuntimeException("Erro BACEN"));
        
        // Executar
        Pix resultado = pixService.processarPix(pix);
        
        // Verificar
        assertNotNull(resultado);
        assertEquals("ERRO", resultado.getStatus());
        assertNotNull(resultado.getMensagemErro());
        assertTrue(resultado.getMensagemErro().contains("Erro BACEN"));
        
        // Verificar interações
        verify(pixValidator, times(1)).validar(pix);
        verify(pixRepositoryPort, times(2)).salvar(any(Pix.class)); // Salva inicial + com erro
        verify(bacenPort, times(1)).enviarTransacao(pix);
    }

    @Test
    void deveAtualizarStatusParaEstornado() {
        // Configurar pix com status PROCESSADO
        pix.setStatus("PROCESSADO");
        pix.setBacenId("BID-12345");
        
        // Executar
        pixService.estornarPix(pix);
        
        // Verificar
        assertEquals("ESTORNADO", pix.getStatus());
        
        // Verificar interações
        verify(pixRepositoryPort, times(1)).salvar(pix);
        verify(bacenPort, times(1)).estornarTransacao("BID-12345");
    }

    @Test
    void deveLidarComErroAoEstornar() {
        // Configurar pix com status PROCESSADO
        pix.setStatus("PROCESSADO");
        pix.setBacenId("BID-12345");
        
        // Configurar erro no BACEN
        doThrow(new RuntimeException("Erro estorno")).when(bacenPort).estornarTransacao(anyString());
        
        // Executar
        pixService.estornarPix(pix);
        
        // Verificar
        assertEquals("ERRO_ESTORNO", pix.getStatus());
        assertNotNull(pix.getMensagemErro());
        assertTrue(pix.getMensagemErro().contains("Erro estorno"));
        
        // Verificar interações
        verify(pixRepositoryPort, times(1)).salvar(pix);
        verify(bacenPort, times(1)).estornarTransacao("BID-12345");
    }
}
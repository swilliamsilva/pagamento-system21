package com.pagamento.pix.infrastructure.integration;

import com.pagamento.pix.config.BacenConfig;
import com.pagamento.pix.core.ports.out.BacenPort;
import com.pagamento.pix.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BacenClientTest {

    @Mock private RestTemplate restTemplate;
    @Mock private BacenConfig bacenConfig;
    @Mock private RetryTemplate retryTemplate;
    
    @InjectMocks private BacenClient bacenClient;
    
    private Pix pix;
    private String bacenId = "BID-12345";

    @BeforeEach
    void setUp() {
        when(bacenConfig.getBacenApiUrl()).thenReturn("https://bacen-api.com");
        when(bacenConfig.getBacenApiKey()).thenReturn("api-key-secret");
        
        Participante pagador = new Participante();
        pagador.setDocumento("12345678909");
        pagador.setNome("João Silva");
        
        Participante recebedor = new Participante();
        recebedor.setDocumento("98765432100");
        recebedor.setNome("Maria Souza");
        
        pix = new Pix();
        pix.setId("PIX-123");
        pix.setValor(new BigDecimal("150.99"));
        pix.setDataTransacao(LocalDateTime.now());
        pix.setPagador(pagador);
        pix.setRecebedor(recebedor);
        pix.setChaveOrigem(new ChavePix("12345678909"));
        pix.setChaveDestino(new ChavePix("maria@email.com"));
        
        // Configurar retry template
        when(retryTemplate.execute(any(), any())).thenAnswer(invocation -> {
            return invocation.getArgument(0).doWithRetry(null);
        });
    }

    // Testes para enviarTransacao
    @Test
    void deveEnviarTransacaoComSucesso() {
        BacenPixResponse response = new BacenPixResponse();
        response.setId(bacenId);
        
        when(restTemplate.exchange(
            anyString(), 
            eq(HttpMethod.POST), 
            any(HttpEntity.class), 
            eq(BacenPixResponse.class)
        )).thenReturn(new ResponseEntity<>(response, HttpStatus.CREATED));
        
        String result = bacenClient.enviarTransacao(pix);
        
        assertEquals(bacenId, result);
        verify(restTemplate).exchange(
            contains("/transacoes"),
            eq(HttpMethod.POST),
            argThat(entity -> hasValidHeaders(entity)),
            eq(BacenPixResponse.class)
        );
    }

    @Test
    void deveLidarComErroHttpNoEnvio() {
        HttpClientErrorException exception = 
            new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");
        
        when(restTemplate.exchange(
            anyString(), 
            any(), 
            any(), 
            any(Class.class)
        )).thenThrow(exception);
        
        BacenIntegrationException ex = assertThrows(
            BacenIntegrationException.class, 
            () -> bacenClient.enviarTransacao(pix)
        );
        
        assertTrue(ex.getMessage().contains("400"));
    }

    @Test
    void deveLidarComRespostaInesperada() {
        when(restTemplate.exchange(
            anyString(), 
            any(), 
            any(), 
            any(Class.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        
        BacenIntegrationException ex = assertThrows(
            BacenIntegrationException.class, 
            () -> bacenClient.enviarTransacao(pix)
        );
        
        assertTrue(ex.getMessage().contains("inesperada"));
    }

    // Testes para estornarTransacao
    @Test
    void deveEstornarTransacaoComSucesso() {
        when(restTemplate.exchange(
            contains("/estornos/" + bacenId),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Void.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.ACCEPTED));
        
        assertDoesNotThrow(() -> bacenClient.estornarTransacao(bacenId));
    }

    @Test
    void deveLidarComErroNoEstorno() {
        HttpClientErrorException exception = 
            new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found");
        
        when(restemplate.exchange(
            anyString(), 
            any(), 
            any(), 
            any(Class.class)
        )).thenThrow(exception);
        
        BacenIntegrationException ex = assertThrows(
            BacenIntegrationException.class, 
            () -> bacenClient.estornarTransacao(bacenId)
        );
        
        assertTrue(ex.getMessage().contains("404"));
    }

    @Test
    void deveUsarRetryNoEnvio() {
        ResourceAccessException exception = new ResourceAccessException("Timeout");
        BacenPixResponse response = new BacenPixResponse();
        response.setId(bacenId);
        
        when(restTemplate.exchange(
            anyString(), 
            any(), 
            any(), 
            any(Class.class)
        ))
        .thenThrow(exception)
        .thenReturn(new ResponseEntity<>(response, HttpStatus.CREATED));
        
        when(retryTemplate.execute(any(), any())).thenCallRealMethod();
        
        String result = bacenClient.enviarTransacao(pix);
        
        assertEquals(bacenId, result);
        verify(restTemplate, times(2)).exchange(any(), any(), any(), any());
    }

    private boolean hasValidHeaders(HttpEntity<?> entity) {
        HttpHeaders headers = entity.getHeaders();
        return "api-key-secret".equals(headers.getFirst("x-api-key")) &&
               MediaType.APPLICATION_JSON.equals(headers.getContentType());
    }
}
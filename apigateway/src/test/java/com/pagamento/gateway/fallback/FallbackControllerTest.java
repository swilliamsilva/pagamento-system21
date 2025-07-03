package com.pagamento.gateway.fallback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Testes para o controlador de fallback do Gateway.
 * 
 * <p>Verifica o comportamento do controlador quando serviços estão indisponíveis,
 * incluindo formatação de respostas, mensagens personalizadas e mecanismos de recuperação.</p>
 */
@ExtendWith(MockitoExtension.class)
class FallbackControllerTest {

    @InjectMocks
    private FallbackController fallbackController;

    /**
     * Verifica se a resposta de fallback para o serviço Pix está correta.
     * 
     * <p>Valida:
     * <ul>
     *   <li>Status HTTP 503 (Service Unavailable)</li>
     *   <li>Mensagem específica para o serviço Pix</li>
     *   <li>Campos obrigatórios na resposta (status, error, message, service, timestamp)</li>
     *   <li>Estimativa de recuperação presente</li>
     * </ul>
     */
    @Test
    void deveRetornarRespostaCorretaParaServicoPix() {
        ResponseEntity<Map<String, Object>> response = 
            fallbackController.serviceFallback("pixservice", 0);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode(), "Status deve ser 503");
        Map<String, Object> body = response.getBody();
        
        assertNotNull(body, "O corpo da resposta não deve ser nulo");
        assertEquals(503, body.get("status"), "Código de status deve ser 503");
        assertEquals("Service Unavailable", body.get("error"), "Tipo de erro deve ser 'Service Unavailable'");
        assertEquals("Serviço de Controle de Pix está indisponível no momento. Tente novamente mais tarde.", 
                     body.get("message"), "Mensagem deve ser específica para o serviço Pix");
        assertEquals("pixservice", body.get("service"), "Nome do serviço deve ser 'pixservice'");
        assertTrue(body.containsKey("timestamp"), "Timestamp deve estar presente");
        assertTrue(body.containsKey("recovery_estimate"), "Estimativa de recuperação deve estar presente");
    }

    /**
     * Verifica se a resposta para serviços desconhecidos está correta.
     * 
     * <p>Valida:
     * <ul>
     *   <li>Mensagem genérica para serviços não mapeados</li>
     *   <li>Header Retry-After com valor padrão de 30 segundos</li>
     *   <li>Nome do serviço retornado no corpo da resposta</li>
     * </ul>
     */
    @Test
    void deveRetornarRespostaGenericaParaServicosDesconhecidos() {
        ResponseEntity<Map<String, Object>> response = 
            fallbackController.serviceFallback("unknown-service", 0);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode(), "Status deve ser 503");
        Map<String, Object> body = response.getBody();
        
        assertNotNull(body, "O corpo da resposta não deve ser nulo");
        assertEquals(503, body.get("status"), "Código de status deve ser 503");
        assertEquals("Service Unavailable", body.get("error"), "Tipo de erro deve ser 'Service Unavailable'");
        assertEquals("Serviço está indisponível no momento. Tente novamente mais tarde.", 
                     body.get("message"), "Mensagem deve ser genérica");
        assertEquals("unknown-service", body.get("service"), "Nome do serviço deve ser 'unknown-service'");
        assertEquals("30", response.getHeaders().getFirst("Retry-After"), "Header Retry-After deve ser 30");
    }

    /**
     * Verifica se todos os serviços definidos têm mensagens personalizadas.
     * 
     * <p>Testa:
     * <ul>
     *   <li>Todos os serviços pré-mapeados no controlador</li>
     *   <li>Presença de mensagem de indisponibilidade</li>
     *   <li>Status HTTP consistente (503)</li>
     * </ul>
     */
    @Test
    void deveTratarTodosServicosDefinidosComMensagensPersonalizadas() {
        String[] services = {"pix", "auth", "assasintegration", "authservice", 
                             "boletoservice", "cardservice", "cloudaws", 
                             "common", "paymentservice", "pixservice"};
        
        for (String service : services) {
            ResponseEntity<Map<String, Object>> response = 
                fallbackController.serviceFallback(service, 0);
            
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode(),
                         "Status deve ser 503 para o serviço: " + service);
            
            Map<String, Object> body = response.getBody();
            assertNotNull(body, "Corpo não deve ser nulo para o serviço: " + service);
            
            String message = (String) body.get("message");
            assertTrue(message != null && message.contains("indisponível"),
                      "Mensagem deve conter 'indisponível' para o serviço: " + service);
        }
    }
    
    /**
     * Verifica se o backoff exponencial é aplicado corretamente.
     * 
     * <p>Valida:
     * <ul>
     *   <li>Tempo de retry maior que 30 segundos para tentativa 2</li>
     *   <li>Contador de tentativas presente na resposta</li>
     *   <li>Cálculo exponencial do tempo de espera</li>
     * </ul>
     */
    @Test
    void deveAplicarBackoffExponencialNasTentativas() {
        int tentativa = 2;
        ResponseEntity<Map<String, Object>> response = 
            fallbackController.serviceFallback("pixservice", tentativa);
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body, "O corpo da resposta não deve ser nulo");
        
        int retryAfter = (int) body.get("retry_after_seconds");
        assertTrue(retryAfter > 30, "Tempo de retry deve ser maior que 30s para tentativa " + tentativa);
        assertEquals(tentativa, body.get("retry_count"), "Contador de tentativas deve ser " + tentativa);
    }

    /**
     * Verifica se o tempo máximo de backoff é respeitado.
     * 
     * <p>Valida que mesmo com alto número de tentativas,
     * o tempo de retry não excede o limite máximo configurado.
     */
    @Test
    void deveRespeitarTempoMaximoDeBackoff() {
        int tentativa = 10; // Número alto de tentativas
        ResponseEntity<Map<String, Object>> response = 
            fallbackController.serviceFallback("pixservice", tentativa);
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body, "O corpo da resposta não deve ser nulo");
        
        int retryAfter = (int) body.get("retry_after_seconds");
        assertTrue(retryAfter <= 300, "Tempo de retry não deve exceder 300s (5 minutos)");
    }
}
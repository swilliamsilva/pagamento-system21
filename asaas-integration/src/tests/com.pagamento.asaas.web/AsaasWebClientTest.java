package com.pagamento.asaas.web;

import com.pagamento.asaas.config.AsaasConfig;
import com.pagamento.asaas.dto.CobrancaRequestDTO;
import com.pagamento.asaas.dto.CobrancaResponseDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

class AsaasWebClientTest {
    private MockWebServer mockWebServer;
    private AsaasWebClient webClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        
        AsaasConfig config = new AsaasConfig("test-key", mockWebServer.url("/").toString());
        webClient = new AsaasWebClient(config);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void criarCobranca_Success() {
        String responseBody = "{\"id\":\"pay_123\",\"status\":\"PENDING\",\"invoiceUrl\":\"http://invoice.url\"}";
        mockWebServer.enqueue(new MockResponse()
            .setBody(responseBody)
            .addHeader("Content-Type", "application/json"));
        
        CobrancaRequestDTO request = new CobrancaRequestDTO();
        request.setCustomerId("cus_123");
        request.setBillingType("BOLETO");
        request.setValue(100.0);
        request.setDueDate("2023-12-31");
        
        Mono<CobrancaResponseDTO> response = webClient.criarCobranca(request);
        
        StepVerifier.create(response)
            .expectNextMatches(dto -> 
                "pay_123".equals(dto.getId()) && 
                "PENDING".equals(dto.getStatus()))
            .verifyComplete();
    }

    @Test
    void criarCobranca_Failure() {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(HttpStatus.BAD_REQUEST.value())
            .setBody("{\"errors\":[{\"code\":\"invalid_value\",\"description\":\"Value must be greater than 0\"}]}")
            .addHeader("Content-Type", "application/json"));
        
        CobrancaRequestDTO request = new CobrancaRequestDTO();
        request.setValue(-1.0);
        
        Mono<CobrancaResponseDTO> response = webClient.criarCobranca(request);
        
        StepVerifier.create(response)
            .verifyErrorMatches(ex -> 
                ex.getMessage().contains("Erro ao criar cobrança:"));
    }
}
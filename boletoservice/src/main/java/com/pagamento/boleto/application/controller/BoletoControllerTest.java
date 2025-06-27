package com.pagamento.boleto.application.controller;

import com.pagamento.boleto.application.dto.BoletoResponseDTO;
import com.pagamento.boleto.domain.exception.BoletoNotFoundException;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.ports.BoletoServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoletoController.class)
@ActiveProfiles("test")
@DisplayName("Testes de Integração do BoletoController")
class BoletoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoletoServicePort boletoService;

    private final String ID_VALIDO = UUID.randomUUID().toString();
    private Boleto boletoStub;
    private BoletoResponseDTO responseStub;

    @BeforeEach
    void setup() {
        boletoStub = new Boleto();
        boletoStub.setId(ID_VALIDO);
        boletoStub.setPagador("Cliente A");
        boletoStub.setBeneficiario("Beneficiário B");
        boletoStub.setValor(new BigDecimal("1000.00"));
        boletoStub.setDataVencimento(LocalDate.now().plusDays(30));
        boletoStub.setStatus(BoletoStatus.EMITIDO);
        
        responseStub = new BoletoResponseDTO(
            ID_VALIDO,
            "Cliente A",
            "Beneficiário B",
            1000.00,
            LocalDate.now().plusDays(30),
            LocalDate.now(),
            "DOC-123",
            "Instruções",
            "Pagável em qualquer banco",
            BoletoStatus.EMITIDO,
            null,
            0,
            null
        );
    }

    @Test
    @DisplayName("POST /api/boletos - Deve criar novo boleto")
    void emitirBoleto_DeveRetornarCreated() throws Exception {
        // Arrange
        String requestBody = """
            {
                "pagador": "Cliente A",
                "beneficiario": "Beneficiário B",
                "valor": 1000.0,
                "dataVencimento": "%s",
                "documento": "DOC-123",
                "instrucoes": "Instruções",
                "localPagamento": "Pagável em qualquer banco"
            }
            """.formatted(LocalDate.now().plusDays(30));
        
        Mockito.when(boletoService.emitirBoleto(any()))
               .thenReturn(boletoStub);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/boletos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(ID_VALIDO)))
                .andExpect(jsonPath("$.pagador", is("Cliente A")))
                .andExpect(jsonPath("$.valor", is(1000.0)))
                .andExpect(jsonPath("$.status", is("EMITIDO")));
    }

    @Test
    @DisplayName("POST /api/boletos - Deve retornar erro para dados inválidos")
    void emitirBoleto_DadosInvalidos_DeveRetornarBadRequest() throws Exception {
        // Arrange
        String invalidRequestBody = """
            {
                "pagador": "",
                "beneficiario": "",
                "valor": -100,
                "dataVencimento": "2020-01-01"
            }
            """;

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/boletos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("GET /api/boletos/{id} - Deve retornar boleto existente")
    void consultarBoleto_Existente_DeveRetornarOk() throws Exception {
        // Arrange
        Mockito.when(boletoService.consultarBoleto(ID_VALIDO))
               .thenReturn(boletoStub);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/boletos/{id}", ID_VALIDO))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ID_VALIDO)))
                .andExpect(jsonPath("$.pagador", is("Cliente A")));
    }

    @Test
    @DisplayName("GET /api/boletos/{id} - Deve retornar 404 para boleto inexistente")
    void consultarBoleto_Inexistente_DeveRetornarNotFound() throws Exception {
        // Arrange
        Mockito.when(boletoService.consultarBoleto(anyString()))
               .thenThrow(new BoletoNotFoundException("Boleto não encontrado"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/boletos/{id}", "ID_INEXISTENTE"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("não encontrado")));
    }

    @Test
    @DisplayName("POST /api/boletos/{id}/cancelar - Deve cancelar boleto com sucesso")
    void cancelarBoleto_DeveRetornarNoContent() throws Exception {
        // Arrange
        Mockito.doNothing().when(boletoService).cancelarBoleto(anyString(), anyString());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/boletos/{id}/cancelar", ID_VALIDO)
                .param("motivo", "Cancelamento solicitado"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/boletos/{id}/reemitir - Deve reemitir boleto com sucesso")
    void reemitirBoleto_DeveRetornarCreated() throws Exception {
        // Arrange
        Boleto reemissao = new Boleto();
        reemissao.setId(UUID.randomUUID().toString());
        reemissao.setStatus(BoletoStatus.REEMITIDO);
        
        Mockito.when(boletoService.reemitirBoleto(ID_VALIDO))
               .thenReturn(reemissao);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/boletos/{id}/reemitir", ID_VALIDO))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", not(ID_VALIDO)))
                .andExpect(jsonPath("$.status", is("REEMITIDO")));
    }

    @Test
    @DisplayName("GET /api/boletos/{id}/pdf - Deve retornar PDF do boleto")
    void gerarPdfBoleto_DeveRetornarPdf() throws Exception {
        // Arrange
        byte[] pdfContent = "PDF Content".getBytes();
        Mockito.when(boletoService.gerarPDF(ID_VALIDO))
               .thenReturn(pdfContent);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/boletos/{id}/pdf", ID_VALIDO))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().bytes(pdfContent))
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"boleto.pdf\""));
    }

    @Test
    @DisplayName("GET /api/boletos/{id}/codigo-barras - Deve retornar código de barras")
    void obterCodigoBarras_DeveRetornarString() throws Exception {
        // Arrange
        String codigoBarras = "34191.23457 89012.345678 90123.456789 3 44700001234567";
        Mockito.when(boletoService.gerarCodigoBarras(ID_VALIDO))
               .thenReturn(codigoBarras);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/boletos/{id}/codigo-barras", ID_VALIDO))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(codigoBarras));
    }

    @Test
    @DisplayName("GET /api/boletos/{id}/qr-code - Deve retornar QR Code")
    void obterQrCode_DeveRetornarString() throws Exception {
        // Arrange
        String qrCode = "00020126580014BR.GOV.BCB.PIX0136123...";
        Mockito.when(boletoService.gerarQRCode(ID_VALIDO))
               .thenReturn(qrCode);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/boletos/{id}/qr-code", ID_VALIDO))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(qrCode));
    }

    @Test
    @DisplayName("POST /api/boletos/{id}/cancelar - Deve retornar erro quando boleto não existe")
    void cancelarBoleto_Inexistente_DeveRetornarNotFound() throws Exception {
        // Arrange
        Mockito.doThrow(new BoletoNotFoundException("Boleto não encontrado"))
               .when(boletoService).cancelarBoleto(anyString(), anyString());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/boletos/{id}/cancelar", "ID_INEXISTENTE")
                .param("motivo", "Motivo qualquer"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
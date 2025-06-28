package com.pagamento.boleto.application.controller;

import com.pagamento.boleto.application.dto.BoletoResponseDTO;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.ports.BoletoServicePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoletoController.class)
@ActiveProfiles("test")
@DisplayName("Testes Adicionais do BoletoController")
class BoletoControllerAdditionalTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoletoServicePort boletoService;

    private final String ID_VALIDO = UUID.randomUUID().toString();
    
    // Teste de Paginação
    @Test
    @WithMockUser
    @DisplayName("GET /api/boletos - Deve retornar lista paginada de boletos")
    void listarBoletos_DeveRetornarListaPaginada() throws Exception {
        // Arrange
        BoletoResponseDTO response = new BoletoResponseDTO(
            ID_VALIDO, "Cliente", "Beneficiário", 100.0, 
            LocalDate.now().plusDays(30), LocalDate.now(), 
            "DOC", "Inst", "Local", BoletoStatus.EMITIDO, 
            null, 0, null
        );
        
        Page<BoletoResponseDTO> page = new PageImpl<>(List.of(response));
        Mockito.when(boletoService.listarBoletos(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/boletos")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "dataVencimento,desc"))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(ID_VALIDO)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)));
    }

    // Teste de Segurança
    @Test
    @DisplayName("Acesso sem autenticação deve retornar Unauthorized")
    void acessoSemAutenticacao_DeveRetornarUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/boletos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Acesso com usuário autenticado deve ter sucesso")
    void acessoComUsuarioAutenticado_DeveTerSucesso() throws Exception {
        Mockito.when(boletoService.listarBoletos(any(Pageable.class)))
               .thenReturn(new PageImpl<>(Collections.emptyList()));
        
        mockMvc.perform(MockMvcRequestBuilders.get("/api/boletos"))
                .andExpect(status().isOk());
    }

    // Teste de Validação Customizada
    @Test
    @WithMockUser
    @DisplayName("POST /api/boletos - Deve rejeitar data de vencimento passada")
    void criarBoletoComDataPassada_DeveRetornarErro() throws Exception {
        // Arrange
        String requestBody = """
            {
                "pagador": "Cliente A",
                "beneficiario": "Beneficiário B",
                "valor": 1000.0,
                "dataVencimento": "%s"
            }
            """.formatted(LocalDate.now().minusDays(1));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/boletos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(csrf())) // CSRF necessário para POST
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("dataVencimento")))
                .andExpect(jsonPath("$.errors[0].message", containsString("futura")));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/boletos - Deve rejeitar valor negativo")
    void criarBoletoComValorNegativo_DeveRetornarErro() throws Exception {
        // Arrange
        String requestBody = """
            {
                "pagador": "Cliente A",
                "beneficiario": "Beneficiário B",
                "valor": -100.0,
                "dataVencimento": "%s"
            }
            """.formatted(LocalDate.now().plusDays(1));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/boletos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field", is("valor")))
                .andExpect(jsonPath("$.errors[0].message", containsString("positivo")));
    }

    // Teste de Filtros
    @Test
    @WithMockUser
    @DisplayName("GET /api/boletos - Deve filtrar por status")
    void listarBoletosPorStatus_DeveRetornarFiltrado() throws Exception {
        // Arrange
        BoletoResponseDTO response = new BoletoResponseDTO(
            ID_VALIDO, "Cliente", "Beneficiário", 100.0, 
            LocalDate.now().plusDays(30), LocalDate.now(), 
            "DOC", "Inst", "Local", BoletoStatus.PAGO, 
            null, 0, null
        );
        
        Page<BoletoResponseDTO> page = new PageImpl<>(List.of(response));
        Mockito.when(boletoService.listarPorStatus(eq(BoletoStatus.PAGO), any(Pageable.class)))
               .thenReturn(page);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/boletos")
                .param("status", "PAGO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status", is("PAGO")));
    }

    // Teste de Atualização de Status em Massa
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/boletos/atualizar-status - Deve atualizar status em massa")
    void atualizarStatusEmMassa_DeveRetornarSucesso() throws Exception {
        // Arrange
        String requestBody = """
            {
                "ids": ["%s", "%s"],
                "status": "CANCELADO"
            }
            """.formatted(UUID.randomUUID(), UUID.randomUUID());

        Mockito.doNothing().when(boletoService).atualizarStatusEmMassa(any(), any());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/boletos/atualizar-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    // Teste de Pesquisa Complexa
    @Test
    @WithMockUser
    @DisplayName("GET /api/boletos/pesquisa - Deve pesquisar com múltiplos critérios")
    void pesquisarBoletos_DeveRetornarResultados() throws Exception {
        // Arrange
        BoletoResponseDTO response = new BoletoResponseDTO(
            ID_VALIDO, "Cliente Especial", "Beneficiário", 500.0, 
            LocalDate.now().plusDays(15), LocalDate.now(), 
            "DOC-123", "Inst", "Local", BoletoStatus.EMITIDO, 
            null, 0, null
        );
        
        Mockito.when(boletoService.pesquisarBoletos(any(), any(), any(), any(), any()))
               .thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/boletos/pesquisa")
                .param("pagador", "Especial")
                .param("valorMin", "100")
                .param("valorMax", "1000")
                .param("dataInicio", LocalDate.now().minusDays(10).toString())
                .param("dataFim", LocalDate.now().plusDays(20).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pagador", containsString("Especial")));
    }
}
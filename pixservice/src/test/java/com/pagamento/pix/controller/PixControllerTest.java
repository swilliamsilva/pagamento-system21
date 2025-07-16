package com.pagamento.pix.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagamento.pix.application.dto.PixRequestDTO;
import com.pagamento.pix.application.dto.PixResponseDTO;
import com.pagamento.pix.application.mapper.PixMapper;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.service.PixService;

@ExtendWith(MockitoExtension.class)
class PixControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Mock
    private PixService pixService;
    
    @Mock
    private PixMapper pixMapper;
    
    @InjectMocks
    private PixController pixController;

    @Test
    void shouldCreatePixSuccessfully() throws Exception {
        // Dados de entrada
        PixRequestDTO request = new PixRequestDTO();
        request.setChaveOrigem("52998224725");
        request.setChaveDestino("user@example.com");
        request.setValor(BigDecimal.valueOf(100.0));
        
        // Entidade de domínio
        Pix pix = new Pix();
        pix.setId("PIX-123");
        pix.setChaveOrigem(request.getChaveOrigem());
        pix.setChaveDestino(request.getChaveDestino());
        pix.setValor(request.getValor());
        
        // Resposta esperada
        PixResponseDTO responseDTO = new PixResponseDTO();
        responseDTO.setId("PIX-123");
        
        // Mocking
        when(pixMapper.toDomain(any(PixRequestDTO.class))).thenReturn(pix);
        when(pix.isValid()).thenReturn(true);
        when(pixService.processarPix(any(Pix.class))).thenReturn(pix);
        when(pixMapper.toResponse(any(Pix.class))).thenReturn(responseDTO);
        
        // Executar e validar
        mockMvc = MockMvcBuilders.standaloneSetup(pixController).build();
        
        mockMvc.perform(post("/api/v1/pix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PIX-123"));
    }

    @Test
    void shouldReturnUnprocessableEntityWhenInvalidPix() throws Exception {
        // Dados de entrada válidos
        PixRequestDTO request = new PixRequestDTO();
        request.setChaveOrigem("valid@cpf.com");
        request.setChaveDestino("user@example.com");
        request.setValor(BigDecimal.valueOf(100.0));
        
        // Entidade de domínio inválida
        Pix invalidPix = new Pix();
        invalidPix.setChaveOrigem("valid@cpf.com");
        invalidPix.setChaveDestino("user@example.com");
        invalidPix.setValor(BigDecimal.valueOf(-10)); // Valor inválido
        
        // Mocking
        when(pixMapper.toDomain(any(PixRequestDTO.class))).thenReturn(invalidPix);
        when(invalidPix.isValid()).thenReturn(false);
        
        // Executar e validar
        mockMvc = MockMvcBuilders.standaloneSetup(pixController).build();
        
        mockMvc.perform(post("/api/v1/pix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldReturnBadRequestWhenInvalidInput() throws Exception {
        // Dados de entrada inválidos (valor ausente)
        PixRequestDTO invalidRequest = new PixRequestDTO();
        invalidRequest.setChaveOrigem("52998224725");
        invalidRequest.setChaveDestino("user@example.com");
        // Valor não definido → inválido
        
        // Executar e validar
        mockMvc = MockMvcBuilders.standaloneSetup(pixController).build();
        
        mockMvc.perform(post("/api/v1/pix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
        // Dados de entrada válidos
        PixRequestDTO request = new PixRequestDTO();
        request.setChaveOrigem("52998224725");
        request.setChaveDestino("user@example.com");
        request.setValor(BigDecimal.valueOf(100.0));
        
        // Entidade de domínio
        Pix pix = new Pix();
        pix.setChaveOrigem(request.getChaveOrigem());
        pix.setChaveDestino(request.getChaveDestino());
        pix.setValor(request.getValor());
        
        // Mocking para lançar exceção
        when(pixMapper.toDomain(any(PixRequestDTO.class))).thenReturn(pix);
        when(pix.isValid()).thenReturn(true);
        when(pixService.processarPix(any(Pix.class)))
            .thenThrow(new RuntimeException("Erro simulado no serviço"));
        
        // Executar e validar
        mockMvc = MockMvcBuilders.standaloneSetup(pixController).build();
        
        mockMvc.perform(post("/api/v1/pix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldRejectWhenOriginEqualsDestination() throws Exception {
        // Dados de entrada com chaves iguais
        PixRequestDTO request = new PixRequestDTO();
        request.setChaveOrigem("same@key.com");
        request.setChaveDestino("same@key.com");
        request.setValor(BigDecimal.valueOf(100.0));
        
        // Entidade de domínio inválida
        Pix invalidPix = new Pix();
        invalidPix.setChaveOrigem("same@key.com");
        invalidPix.setChaveDestino("same@key.com");
        invalidPix.setValor(BigDecimal.valueOf(100.0));
        
        // Mocking
        when(pixMapper.toDomain(any(PixRequestDTO.class))).thenReturn(invalidPix);
        when(invalidPix.isValid()).thenReturn(false);
        
        // Executar e validar
        mockMvc = MockMvcBuilders.standaloneSetup(pixController).build();
        
        mockMvc.perform(post("/api/v1/pix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }
}
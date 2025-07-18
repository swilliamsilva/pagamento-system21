package com.pagamento.boleto;

import org.junit.jupiter.api.Test;

import com.pagamento.boleto.application.dto.BoletoResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BoletoResponseDTOTest {

    @Test
    void shouldCreateBoletoResponseDTOWithAllFields() {
        // Arrange
        String id = UUID.randomUUID().toString();
        String pagador = "Cliente A";
        String beneficiario = "Beneficiário B";
        BigDecimal valor = new BigDecimal("1000.00");
        LocalDate dataVencimento = LocalDate.now().plusDays(30);
        LocalDate dataEmissao = LocalDate.now();
        String documento = "DOC-123";
        String instrucoes = "Instruções de pagamento";
        String localPagamento = "Qualquer banco";
        String status = "EMITIDO";
        String motivoCancelamento = null;
        int numeroReemissoes = 0;
        String boletoOriginalId = null;

        // Act
        BoletoResponseDTO response = new BoletoResponseDTO(
            id, pagador, beneficiario, valor, dataVencimento, dataEmissao,
            documento, instrucoes, localPagamento, status, motivoCancelamento,
            numeroReemissoes, boletoOriginalId
        );

        // Assert
        assertAll(
            () -> assertEquals(id, response.id()),
            () -> assertEquals(pagador, response.pagador()),
            () -> assertEquals(beneficiario, response.beneficiario()),
            () -> assertEquals(valor, response.valor()),
            () -> assertEquals(dataVencimento, response.dataVencimento()),
            () -> assertEquals(dataEmissao, response.dataEmissao()),
            () -> assertEquals(documento, response.documento()),
            () -> assertEquals(instrucoes, response.instrucoes()),
            () -> assertEquals(localPagamento, response.localPagamento()),
            () -> assertEquals(status, response.status()),
            () -> assertNull(response.motivoCancelamento()),
            () -> assertEquals(0, response.numeroReemissoes()),
            () -> assertNull(response.boletoOriginalId())
        );
    }

    @Test
    void shouldHandleReissuedBoletoCorrectly() {
        // Arrange
        String originalId = UUID.randomUUID().toString();
        
        // Act
        BoletoResponseDTO response = new BoletoResponseDTO(
            UUID.randomUUID().toString(),
            "Cliente C",
            "Beneficiário D",
            new BigDecimal("500.00"),
            LocalDate.now().plusDays(15),
            LocalDate.now(),
            "DOC-456",
            "Nenhuma",
            "Banco específico",
            "REEMITIDO",
            null,
            1,
            originalId
        );

        // Assert
        assertAll(
            () -> assertEquals("REEMITIDO", response.status()),
            () -> assertEquals(1, response.numeroReemissoes()),
            () -> assertEquals(originalId, response.boletoOriginalId())
        );
    }

    @Test
    void shouldHandleCanceledBoletoCorrectly() {
        // Act
        BoletoResponseDTO response = new BoletoResponseDTO(
            UUID.randomUUID().toString(),
            "Cliente E",
            "Beneficiário F",
            new BigDecimal("750.00"),
            LocalDate.now().plusDays(20),
            LocalDate.now().minusDays(5),
            "DOC-789",
            "Pagamento atrasado",
            "Agência central",
            "CANCELADO",
            "Solicitação do cliente",
            0,
            null
        );

        // Assert
        assertAll(
            () -> assertEquals("CANCELADO", response.status()),
            () -> assertEquals("Solicitação do cliente", response.motivoCancelamento())
        );
    }

    @Test
    void shouldHandleNullValuesSafely() {
        // Act
        BoletoResponseDTO response = new BoletoResponseDTO(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0,
            null
        );

        // Assert
        assertAll(
            () -> assertNull(response.id()),
            () -> assertNull(response.pagador()),
            () -> assertNull(response.valor()),
            () -> assertNull(response.dataVencimento())
        );
    }
}
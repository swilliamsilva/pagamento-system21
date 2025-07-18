package com.pagamento.boleto;

import com.pagamento.boleto.application.dto.BoletoResponseDTO;
import com.pagamento.boleto.application.mapper.BoletoMapper;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BoletoMapperTest {

    @Test
    void toDTO_ShouldMapAllFieldsCorrectly() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDate now = LocalDate.now();
        
        Boleto boleto = new Boleto();
        boleto.setId(id.toString());
        boleto.setPagador("Cliente A");
        boleto.setBeneficiario("Beneficiário B");
        boleto.setValor(BigDecimal.valueOf(1500.75));
        boleto.setDataVencimento(now.plusDays(30));
        boleto.setDataEmissao(now);
        boleto.setStatus(BoletoStatus.EMITIDO);
        boleto.setDocumento("DOC-123456");
        boleto.setInstrucoes("Aceitar até o vencimento");
        boleto.setLocalPagamento("Qualquer agência bancária");
        boleto.setMotivoCancelamento(null);
        boleto.setNumeroReemissoes(0);
        boleto.setBoletoOriginalId(null);

        // Act
        BoletoResponseDTO dto = BoletoMapper.toDTO(boleto);

        // Assert
        assertAll(
            () -> assertEquals(id.toString(), dto.id()),
            () -> assertEquals("Cliente A", dto.pagador()),
            () -> assertEquals("Beneficiário B", dto.beneficiario()),
            () -> assertEquals(BigDecimal.valueOf(1500.75), dto.valor()),
            () -> assertEquals(now.plusDays(30), dto.dataVencimento()),
            () -> assertEquals(now, dto.dataEmissao()),
            () -> assertEquals("DOC-123456", dto.documento()),
            () -> assertEquals("Aceitar até o vencimento", dto.instrucoes()),
            () -> assertEquals("Qualquer agência bancária", dto.localPagamento()),
            () -> assertEquals("EMITIDO", dto.status()),
            () -> assertNull(dto.motivoCancelamento()),
            () -> assertEquals(0, dto.numeroReemissoes()),
            () -> assertNull(dto.boletoOriginalId())
        );
    }

    @Test
    void toDTO_ShouldHandleCanceledBoleto() {
        // Arrange
        Boleto boleto = new Boleto();
        boleto.setId(UUID.randomUUID().toString());
        boleto.setStatus(BoletoStatus.CANCELADO);
        boleto.setMotivoCancelamento("Solicitação do cliente");

        // Act
        BoletoResponseDTO dto = BoletoMapper.toDTO(boleto);

        // Assert
        assertEquals("CANCELADO", dto.status());
        assertEquals("Solicitação do cliente", dto.motivoCancelamento());
    }

    @Test
    void toDTO_ShouldHandleReissuedBoleto() {
        // Arrange
        String originalId = UUID.randomUUID().toString();
        
        Boleto boleto = new Boleto();
        boleto.setId(UUID.randomUUID().toString());
        boleto.setStatus(BoletoStatus.REEMITIDO);
        boleto.setNumeroReemissoes(2);
        boleto.setBoletoOriginalId(originalId);

        // Act
        BoletoResponseDTO dto = BoletoMapper.toDTO(boleto);

        // Assert
        assertEquals("REEMITIDO", dto.status());
        assertEquals(2, dto.numeroReemissoes());
        assertEquals(originalId, dto.boletoOriginalId());
    }

    @Test
    void constructor_ShouldBePrivateAndThrowException() throws Exception {
        // Obtém o construtor privado
        Constructor<BoletoMapper> constructor = BoletoMapper.class.getDeclaredConstructor();
        
        // Permite acesso ao construtor privado
        constructor.setAccessible(true);
        
        // Verifica se lança a exceção correta ao tentar instanciar
        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            constructor.newInstance();
        });
        
        // Verifica a causa da exceção
        assertTrue(exception.getCause() instanceof UnsupportedOperationException);
        assertEquals("Esta é uma classe utilitária e não pode ser instanciada", 
                     exception.getCause().getMessage());
    }
}
package com.pagamento.pix.infrastructure.adapters;

import com.pagamento.pix.domain.model.ChavePix;
import com.pagamento.pix.domain.model.Participante;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.domain.model.PixStatus;
import com.pagamento.pix.domain.model.TipoChave;
import com.pagamento.pix.repository.dynamo.PixDynamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PixRepositoryAdapterTest {

    @Mock
    private PixDynamoRepository repository;

    @InjectMocks
    private PixRepositoryAdapter adapter;

    @Captor
    private ArgumentCaptor<Pix> pixCaptor;

    private Pix pix;

    @BeforeEach
    void setUp() {
        Participante pagador = new Participante("João Silva", "12345678900", "Banco A", "Agência 1", "Conta 123");
        Participante recebedor = new Participante("Maria Souza", "98765432100", "Banco B", "Agência 2", "Conta 456");
        ChavePix chaveDestino = new ChavePix(TipoChave.CPF, "98765432100", "Banco B", "Agência 2", "Conta 456");

        pix = Pix.builder()
                .valor(BigDecimal.valueOf(100.0))
                .pagador(pagador)
                .recebedor(recebedor)
                .chaveDestino(chaveDestino)
                .dataTransacao(LocalDateTime.now())
                .status(PixStatus.EM_PROCESSAMENTO)
                .build();
    }

    @Test
    void salvar_deveCriptografarDocumentosDosParticipantes() {
        // Act
        adapter.salvar(pix);

        // Assert
        verify(repository).salvar(pixCaptor.capture());
        Pix pixSalvo = pixCaptor.getValue();

        // Verifica se os documentos originais estão presentes antes da criptografia
        assertEquals("12345678900", pix.getPagador().getDocumento());
        assertEquals("98765432100", pix.getRecebedor().getDocumento());

        // Verifica se os documentos foram criptografados
        assertNotEquals("12345678900", pixSalvo.getPagador().getDocumento());
        assertNotEquals("98765432100", pixSalvo.getRecebedor().getDocumento());
        assertTrue(pixSalvo.getPagador().getDocumento().startsWith("ENC:"));
        assertTrue(pixSalvo.getRecebedor().getDocumento().startsWith("ENC:"));
    }

    @Test
    void salvar_deveManterOutrosDadosIntactos() {
        // Act
        adapter.salvar(pix);

        // Assert
        verify(repository).salvar(pixCaptor.capture());
        Pix pixSalvo = pixCaptor.getValue();

        assertEquals(pix.getValor(), pixSalvo.getValor());
        assertEquals(pix.getStatus(), pixSalvo.getStatus());
        assertEquals(pix.getDataTransacao(), pixSalvo.getDataTransacao());
        assertEquals(pix.getChaveDestino().getTipo(), pixSalvo.getChaveDestino().getTipo());
    }
}
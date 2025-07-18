package com.pagamento.pix.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PixStatusTest {

    @Test
    void descricaoDeveCorresponderAoStatus() {
        assertEquals("Em processamento", PixStatus.EM_PROCESSAMENTO.getDescricao());
        assertEquals("Processado com sucesso", PixStatus.PROCESSADO.getDescricao());
        assertEquals("Transação estornada", PixStatus.ESTORNADO.getDescricao());
    }

    @Test
    void verificaStatusConcluido() {
        assertTrue(PixStatus.PROCESSADO.isConcluido());
        assertTrue(PixStatus.ESTORNADO.isConcluido());
        assertFalse(PixStatus.EM_PROCESSAMENTO.isConcluido());
        assertFalse(PixStatus.ERRO.isConcluido());
    }

    @Test
    void verificaStatusErro() {
        assertTrue(PixStatus.REJEITADO.isErro());
        assertTrue(PixStatus.ERRO.isErro());
        assertTrue(PixStatus.ERRO_ESTORNO.isErro());
        assertFalse(PixStatus.PROCESSADO.isErro());
        assertFalse(PixStatus.VALIDANDO.isErro());
    }

    @Test
    void verificaPermissaoEstorno() {
        assertTrue(PixStatus.PROCESSADO.permiteEstorno());
        assertFalse(PixStatus.EM_PROCESSAMENTO.permiteEstorno());
        assertFalse(PixStatus.ESTORNADO.permiteEstorno());
        assertFalse(PixStatus.REJEITADO.permiteEstorno());
    }

    @Test
    void verificaStatusTerminal() {
        assertTrue(PixStatus.PROCESSADO.isTerminal());
        assertTrue(PixStatus.ESTORNADO.isTerminal());
        assertTrue(PixStatus.REJEITADO.isTerminal());
        assertTrue(PixStatus.ERRO.isTerminal());
        assertTrue(PixStatus.ERRO_ESTORNO.isTerminal());
        
        assertFalse(PixStatus.EM_PROCESSAMENTO.isTerminal());
        assertFalse(PixStatus.VALIDANDO.isTerminal());
        assertFalse(PixStatus.ENVIANDO_BACEN.isTerminal());
        assertFalse(PixStatus.ESTORNANDO.isTerminal());
    }

    @Test
    void valoresDevemSerConsistentes() {
        assertEquals(10, PixStatus.values().length);
    }
}
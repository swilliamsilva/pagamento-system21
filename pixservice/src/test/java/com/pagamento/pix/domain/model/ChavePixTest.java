package com.pagamento.pix.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChavePixTest {

    @Test
    void deveCriarChaveEmailValida() {
        ChavePix chave = new ChavePix("usuario@dominio.com");
        assertEquals(TipoChave.EMAIL, chave.getTipo());
        assertTrue(chave.validar());
    }

    @Test
    void deveCriarChaveCelularValida() {
        ChavePix chave = new ChavePix("+5511999999999");
        assertEquals(TipoChave.CELULAR, chave.getTipo());
        assertTrue(chave.validar());
    }

    @Test
    void deveCriarChaveAleatoriaValida() {
        ChavePix chave = new ChavePix("a1b2c3d4-e5f6-4a0d-9e8f-123456789abc");
        assertEquals(TipoChave.ALEATORIA, chave.getTipo());
        assertTrue(chave.validar());
    }

    @Test
    void deveRejeitarChaveInvalida() {
        assertThrows(IllegalArgumentException.class, () -> 
            new ChavePix("chave-inválida#123")
        );
    }

    @Test
    void deveRejeitarChaveMuitoLonga() {
        String chaveLonga = "a".repeat(78);
        assertThrows(IllegalArgumentException.class, () -> 
            new ChavePix(chaveLonga)
        );
    }

    @Test
    void deveIdentificarTipoDesconhecido() {
        ChavePix chave = new ChavePix("chave_invalida@invalida");
        assertEquals(TipoChave.DESCONHECIDA, chave.getTipo());
        assertFalse(chave.validar());
    }
}
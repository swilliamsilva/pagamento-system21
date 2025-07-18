package com.pagamento.pix.infrastructure.integration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BacenIntegrationExceptionTest {

    @Test
    void deveCriarExcecaoComMensagem() {
        BacenIntegrationException ex = new BacenIntegrationException("Erro teste");
        assertEquals("Erro teste", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void deveCriarExcecaoComMensagemECausa() {
        Throwable cause = new RuntimeException("Causa original");
        BacenIntegrationException ex = new BacenIntegrationException("Erro teste", cause);
        assertEquals("Erro teste", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
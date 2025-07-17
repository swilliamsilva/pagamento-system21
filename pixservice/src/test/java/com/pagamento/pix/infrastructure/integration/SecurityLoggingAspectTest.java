package com.pagamento.pix.infrastructure.integration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SecurityLoggingAspectTest { // Removido o modificador 'public'

    // Instância da classe sendo testada
    private SecurityLoggingAspect aspect = new SecurityLoggingAspect();

    @Test
    void log_deveMascararDocumento() {
        String original = "Documento: 12345678900";
        String masked = aspect.maskSensitiveData(original);
        
        assertTrue(masked.contains("***"), "Deveria conter máscara");
        assertEquals("Documento: ***", masked, "Deveria mascarar o documento");
    }
}
package com.pagamento.payment.tests.integration;

import com.pagamento.payment.config.VaultInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class VaultTokenTest {

    @Autowired
    private VaultInitializer vaultInitializer;

    @Test
    void shouldLoadVaultSuccessfully() {
        assertDoesNotThrow(() -> vaultInitializer.run());
    }
}

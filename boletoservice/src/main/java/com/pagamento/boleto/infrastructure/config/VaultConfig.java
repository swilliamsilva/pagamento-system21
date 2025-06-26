package com.pagamento.boleto.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("vault")
public class VaultConfig {
    // Beans Vault aqui se necessário
}

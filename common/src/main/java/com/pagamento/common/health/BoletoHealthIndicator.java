package com.pagamento.common.health;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health check específico para o módulo de boletos.
 * Verifica: 
 * - Conexão com banco de dados
 * - Serviços essenciais
 * - Filas de processamento (simulado)
 */
@Component
public class BoletoHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;
    private final ExternalBoletoService boletoService; // Serviço hipotético

    @Autowired
    public BoletoHealthIndicator(DataSource dataSource, ExternalBoletoService boletoService) {
        this.dataSource = dataSource;
        this.boletoService = boletoService;
    }

    @Override
    public Health health() {
        Health.Builder healthBuilder = Health.up();
        
        // 1. Verificar conexão com banco
        if (!checkDatabaseConnection()) {
            healthBuilder.down().withDetail("database", "Connection failed");
        }

        // 2. Verificar serviço externo
        if (!boletoService.isHealthy()) {
            healthBuilder.down().withDetail("external_service", "Boleto API unavailable");
        }

        // 3. Verificar fila de processamento (exemplo simulado)
        if (isQueueOverloaded()) {
            healthBuilder.status("WARNING").withDetail("queue", "High backlog detected");
        }

        return healthBuilder.build();
    }

    private boolean checkDatabaseConnection() {
        try {
            return dataSource.getConnection().isValid(2); // Timeout de 2 segundos
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean isQueueOverloaded() {
        // Lógica simulada: normalmente consultaria um broker de mensagens
        final int currentQueueSize = 150;
        final int warningThreshold = 100;
        return currentQueueSize > warningThreshold;
    }
}

// Serviço fictício para demonstração
@Component
class ExternalBoletoService {
    public boolean isHealthy() {
        // Lógica real verificaria API externa
        return true; // Simulação
    }
}
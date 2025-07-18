package com.pagamento.common.health;

import java.sql.Connection;
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
    private final ExternalBoletoService boletoService;

    @Autowired
    public BoletoHealthIndicator(DataSource dataSource, ExternalBoletoService boletoService) {
        this.dataSource = dataSource;
        this.boletoService = boletoService;
    }

    @Override
    public Health health() {
        Health.Builder healthBuilder = Health.up();
        
        if (!checkDatabaseConnection()) {
            healthBuilder.down().withDetail("database", "Connection failed");
        }

        if (!boletoService.isHealthy()) {
            healthBuilder.down().withDetail("external_service", "Boleto API unavailable");
        }

        if (isQueueOverloaded()) {
            healthBuilder.status("WARNING").withDetail("queue", "High backlog detected");
        }

        return healthBuilder.build();
    }

    private boolean checkDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2); // Usando try-with-resources
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean isQueueOverloaded() {
        final int currentQueueSize = 150;
        final int warningThreshold = 100;
        return currentQueueSize > warningThreshold;
    }
}

@Component
class ExternalBoletoService {
    public boolean isHealthy() {
        return true;
    }
}
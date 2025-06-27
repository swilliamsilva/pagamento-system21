package com.pagamento.boleto.domain.model;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public class BoletoStatusHistorico {
    private BoletoStatus status;
    private LocalDateTime dataHora;

    // Construtor padrão necessário para JPA
    public BoletoStatusHistorico() {}

    public BoletoStatusHistorico(BoletoStatus status, LocalDateTime dataHora) {
        this.status = status;
        this.dataHora = dataHora;
    }

    // Getters
    public BoletoStatus getStatus() { return status; }
    public LocalDateTime getDataHora() { return dataHora; }
}
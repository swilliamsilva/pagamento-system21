package com.pagamento.pix.domain.model;

import java.time.LocalDateTime;

public class EstadoPix {
    private final PixStatus statusAnterior;
    private final PixStatus novoStatus;
    private final LocalDateTime dataHora;
    private final String motivo;

    public EstadoPix(PixStatus statusAnterior, PixStatus novoStatus, 
                    LocalDateTime dataHora, String motivo) {
        this.statusAnterior = statusAnterior;
        this.novoStatus = novoStatus;
        this.dataHora = dataHora;
        this.motivo = motivo;
    }

    // Getters
    public PixStatus getStatusAnterior() { return statusAnterior; }
    public PixStatus getNovoStatus() { return novoStatus; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getMotivo() { return motivo; }
}
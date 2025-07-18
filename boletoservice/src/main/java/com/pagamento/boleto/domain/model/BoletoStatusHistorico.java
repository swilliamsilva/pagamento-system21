package com.pagamento.boleto.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class BoletoStatusHistorico {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BoletoStatus status;

    @Column(nullable = false, name = "data_hora")
    private LocalDateTime dataHora;

    // Construtor padrão necessário para JPA
    public BoletoStatusHistorico() {
    }

    // Construtor principal
    public BoletoStatusHistorico(BoletoStatus status, LocalDateTime dataHora) {
        this.status = Objects.requireNonNull(status, "Status não pode ser nulo");
        this.dataHora = Objects.requireNonNull(dataHora, "Data/Hora não pode ser nula");
    }

    // Getters
    public BoletoStatus getStatus() {
        return status;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    // Setters (protegidos se necessário, mas públicos para funcionar com JPA)
    public void setStatus(BoletoStatus status) {
        this.status = status;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    // Equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoletoStatusHistorico that = (BoletoStatusHistorico) o;
        return status == that.status && 
               Objects.equals(dataHora, that.dataHora);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, dataHora);
    }

    @Override
    public String toString() {
        return "BoletoStatusHistorico{" +
                "status=" + status +
                ", dataHora=" + dataHora +
                '}';
    }
}
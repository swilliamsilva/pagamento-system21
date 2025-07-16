package com.pagamento.pix.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class EstornoRequestDTO {

    @NotBlank(message = "ID da transação é obrigatório")
    @Schema(description = "ID da transação PIX a ser estornada", example = "PIX-12345")
    private String pixId;

    @Schema(description = "Motivo do estorno", example = "Pagamento duplicado")
    private String motivo;

    // Getters e Setters
    public String getPixId() {
        return pixId;
    }

    public void setPixId(String pixId) {
        this.pixId = pixId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
/* ========================================================
# Classe: BoletoResponseDTO
# Módulo: boleto-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: DTO de resposta após geração de boleto.
# ======================================================== */

package com.pagamento.boleto.application.dto;

public record BoletoResponseDTO(
    String codigoBarras,
    Double valor,
    String status
) {}

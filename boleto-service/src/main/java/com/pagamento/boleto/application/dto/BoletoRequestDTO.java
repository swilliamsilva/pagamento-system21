/* ========================================================
# Classe: BoletoRequestDTO
# Módulo: boleto-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: DTO de entrada para geração de boletos.
# ======================================================== */

package com.pagamento.boleto.application.dto;

public record BoletoRequestDTO(
    Double valor,
    String descricao,
    String vencimento
) {}

/* ========================================================
# Classe: BoletoRequestDTO
# Módulo: common
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: DTO de requisição para emissão de boleto.
# ======================================================== */

package com.pagamento.common.dto;

import java.math.BigDecimal;

public record BoletoRequestDTO(
    String usuarioId,
    BigDecimal valor,
    String vencimento
) {}

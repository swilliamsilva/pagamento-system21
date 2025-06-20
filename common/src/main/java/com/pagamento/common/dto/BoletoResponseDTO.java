/* ========================================================
# Classe: BoletoResponseDTO
# Módulo: common
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: DTO de resposta do boleto.
# ======================================================== */

package com.pagamento.common.dto;

import java.math.BigDecimal;

public record BoletoResponseDTO(
    String boletoId,
    String codigoBarras,
    BigDecimal valor,
    String vencimento
) {}

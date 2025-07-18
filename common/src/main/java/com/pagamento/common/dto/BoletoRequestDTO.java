/* ========================================================
# Classe: BoletoRequestDTO
# Módulo: common
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: DTO de requisição para emissão de boleto.
# ======================================================== */

package com.pagamento.common.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BoletoRequestDTO(
    @NotBlank(message = "Pagador é obrigatório")
    String pagador,
    
    @NotBlank(message = "Beneficiário é obrigatório")
    String beneficiario,
    
    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    BigDecimal valor,
    
    @NotNull(message = "Data de vencimento é obrigatória")
    @Future(message = "Data de vencimento deve ser futura")
    LocalDate dataVencimento,
    
    String documento,
    String instrucoes,
    String localPagamento
) {}
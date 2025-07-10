package com.pagamento.boleto.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagamentoConfirmadoDTO(
    String idBoleto,
    String documento,
    BigDecimal valor,
    LocalDate dataPagamento
) {}
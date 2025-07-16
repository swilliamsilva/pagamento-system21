package com.pagamento.pix.domain.model;

import java.math.BigDecimal;

@FunctionalInterface
public interface TaxaStrategy {
    double calcular(BigDecimal valor);
}
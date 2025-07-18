package com.pagamento.pix.domain.model;

import java.math.BigDecimal;

public class TaxaProgressivaStrategy implements TaxaStrategy {
    @Override
    public double calcular(BigDecimal valor) {
        if (valor.compareTo(new BigDecimal("1000")) <= 0) {
            return 1.0;
        } else if (valor.compareTo(new BigDecimal("5000")) <= 0) {
            return 5.0;
        } else {
            return valor.multiply(new BigDecimal("0.002")).doubleValue();
        }
    }
}
package com.pagamento.pix.domain.model;

import java.math.BigDecimal;

// Taxa fixa
public class TaxaFixaStrategy implements TaxaStrategy {
    private final double valorFixo;

    public TaxaFixaStrategy(double valorFixo) {
        this.valorFixo = valorFixo;
    }

    @Override
    public double calcular(BigDecimal valor) {
        return valorFixo;
    }
}

// Taxa percentual

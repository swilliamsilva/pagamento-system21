package com.pagamento.pix.domain.model;

import java.math.BigDecimal;

public class TaxaPercentualStrategy implements TaxaStrategy {
    private final double percentual;

    public TaxaPercentualStrategy(double percentual) {
        this.percentual = percentual;
    }

    @Override
    public double calcular(BigDecimal valor) {
        return valor.multiply(BigDecimal.valueOf(percentual)).doubleValue();
    }
}

// Taxa progressiva (exemplo)

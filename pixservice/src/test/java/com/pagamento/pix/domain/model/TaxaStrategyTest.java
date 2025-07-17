package com.pagamento.pix.domain.model;

import org.junit.Test;

import com.pagamento.pix.domain.model.TaxaFixaStrategy;
import com.pagamento.pix.domain.model.TaxaPercentualStrategy;

class TaxaStrategyTest {
    @Test
    void taxaFixa_deveRetornarValorFixo() {
        TaxaStrategy strategy = new TaxaFixaStrategy(5.0);
        assertEquals(5.0, strategy.calcularTaxa(100.0));
    }
    
    @Test
    void taxaPercentual_deveCalcularPercentual() {
        TaxaStrategy strategy = new TaxaPercentualStrategy(0.05); // 5%
        assertEquals(5.0, strategy.calcularTaxa(100.0));
    }
}
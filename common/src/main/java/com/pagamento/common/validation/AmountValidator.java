package com.pagamento.common.validation;

import java.math.BigDecimal;

public class AmountValidator {

    public static boolean isValid(BigDecimal valor) {
        return valor != null && valor.compareTo(BigDecimal.ZERO) > 0;
    }
}

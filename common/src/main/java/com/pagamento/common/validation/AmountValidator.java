package com.pagamento.common.validation;

import java.math.BigDecimal;

public class AmountValidator {
    public boolean isValid(BigDecimal value, Object context) {
        if (value == null) {
            return false;
        }
        return value.compareTo(BigDecimal.ZERO) > 0;
    }
}
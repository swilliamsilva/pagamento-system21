package com.pagamento.common.validation;

import java.math.BigDecimal;

public class AmountValidator {
    public boolean isValid(BigDecimal value) {
        if (value == null) {
            return false;
        }
        return value.compareTo(BigDecimal.ZERO) > 0;
    }
}
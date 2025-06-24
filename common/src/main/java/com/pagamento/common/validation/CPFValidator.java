
// ==========================
// VALIDATOR: CPFValidator.java
// ==========================
package com.pagamento.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validador de CPF com base em algoritmo de verificação.
 */
public class CPFValidator implements ConstraintValidator<ValidCPF, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int sum = 0, weight = 10;
            for (int i = 0; i < 9; i++)
                sum += (cpf.charAt(i) - '0') * weight--;

            int check1 = 11 - (sum % 11);
            if (check1 >= 10) check1 = 0;
            if (check1 != (cpf.charAt(9) - '0')) return false;

            sum = 0; weight = 11;
            for (int i = 0; i < 10; i++)
                sum += (cpf.charAt(i) - '0') * weight--;

            int check2 = 11 - (sum % 11);
            if (check2 >= 10) check2 = 0;
            return check2 == (cpf.charAt(10) - '0');

        } catch (Exception e) {
            return false;
        }
    }
}

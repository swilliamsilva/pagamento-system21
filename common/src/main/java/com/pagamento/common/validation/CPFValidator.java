package com.pagamento.common.validation;

public class CPFValidator {
    public boolean isValid(String value, Object context) {
        if (value == null) {
            return false;
        }
        String cpf = value.replaceAll("\\D", "");
        if (cpf.length() != 11) {
            return false;
        }
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }
        int remainder = sum % 11;
        int digit1 = remainder < 2 ? 0 : 11 - remainder;
        
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * (11 - i);
        }
        remainder = sum % 11;
        int digit2 = remainder < 2 ? 0 : 11 - remainder;
        
        return (digit1 == (cpf.charAt(9) - '0')) && (digit2 == (cpf.charAt(10) - '0'));
    }
}
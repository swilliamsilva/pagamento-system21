package com.pagamento.pix.domain.service;

import java.util.regex.Pattern;

public class DocumentoValidator {

    public static boolean validarCPF(String cpf) {
        if (cpf == null || !Pattern.matches("\\d{11}", cpf)) return false;
        
        int[] digits = cpf.chars().map(Character::getNumericValue).toArray();
        return calcDigCPF(digits, 9) == digits[9] && 
               calcDigCPF(digits, 10) == digits[10];
    }

    private static int calcDigCPF(int[] digits, int length) {
        int weight = length + 1;
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += digits[i] * weight--;
        }
        int remainder = sum % 11;
        return (remainder < 2) ? 0 : (11 - remainder);
    }

    public static boolean validarCNPJ(String cnpj) {
        if (cnpj == null || !Pattern.matches("\\d{14}", cnpj)) return false;
        
        int[] digits = cnpj.chars().map(Character::getNumericValue).toArray();
        return calcDigCNPJ(digits, 12) == digits[12] && 
               calcDigCNPJ(digits, 13) == digits[13];
    }

    private static int calcDigCNPJ(int[] digits, int length) {
        int[] weights = (length == 12) ? 
            new int[]{5,4,3,2,9,8,7,6,5,4,3,2} : 
            new int[]{6,5,4,3,2,9,8,7,6,5,4,3,2};
            
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += digits[i] * weights[i];
        }
        int remainder = sum % 11;
        return (remainder < 2) ? 0 : (11 - remainder);
    }
}
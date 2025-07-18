package com.pagamento.pix.domain.model;

import java.util.regex.Pattern;

public class ChavePix {
    private final String valor;
    private final TipoChave tipo;

    // Limite máximo para chaves PIX conforme especificação do BACEN
    private static final int MAX_LENGTH = 77;
    
    // Padrões pré-compilados otimizados
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[\\w.-]+@[^@\\s]+\\.[^@\\s]{2,}$");
    
    private static final Pattern CELULAR_PATTERN = 
        Pattern.compile("^\\+[1-9]\\d{1,14}$");
    
    private static final Pattern CHAVE_ALEATORIA_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9\\-._@]{1,77}$");
    
    private static final Pattern CPF_PATTERN = 
        Pattern.compile("^\\d{11}$");
    
    private static final Pattern CNPJ_PATTERN = 
        Pattern.compile("^\\d{14}$");

    public ChavePix(String valor) {
        if (valor == null || valor.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Chave PIX inválida: " + valor);
        }
        
        this.valor = valor;
        this.tipo = identificarTipo(valor);
        
        if (!validar()) {
            throw new IllegalArgumentException("Chave PIX inválida: " + valor);
        }
    }

    private TipoChave identificarTipo(String chave) {
        if (isCPF(chave)) return TipoChave.CPF;
        if (isCNPJ(chave)) return TipoChave.CNPJ;
        if (isEmail(chave)) return TipoChave.EMAIL;
        if (isCelular(chave)) return TipoChave.CELULAR;
        if (isChaveAleatoria(chave)) return TipoChave.ALEATORIA;
        return TipoChave.DESCONHECIDA;
    }

    private boolean isCPF(String chave) {
        return chave != null && CPF_PATTERN.matcher(chave).matches();
    }

    private boolean isCNPJ(String chave) {
        return chave != null && CNPJ_PATTERN.matcher(chave).matches();
    }

    public boolean validar() {
        return tipo != TipoChave.DESCONHECIDA;
    }

    private boolean isEmail(String key) {
        if (key == null) return false;
        
        // Validação em duas etapas para evitar backtracking catastrófico
        int atIndex = key.indexOf('@');
        if (atIndex <= 0 || atIndex == key.length() - 1) {
            return false;
        }
        
        String localPart = key.substring(0, atIndex);
        String domainPart = key.substring(atIndex + 1);
        
        // Validação simplificada sem regex complexa
        return isValidLocalPart(localPart) && 
               isValidDomainPart(domainPart) &&
               EMAIL_PATTERN.matcher(key).matches();
    }

    private boolean isValidLocalPart(String localPart) {
        // Local part deve ter entre 1 e 64 caracteres
        if (localPart.isEmpty() || localPart.length() > 64) {
            return false;
        }
        
        // Verificar caracteres permitidos: alfanuméricos e . - _
        for (char c : localPart.toCharArray()) {
            if (!(Character.isLetterOrDigit(c) || c == '.' || c == '-' || c == '_')) {
                return false;
            }
        }
        
        // Substituído if por retorno direto
        return !localPart.startsWith(".") && !localPart.endsWith(".");
    }

    private boolean isValidDomainPart(String domainPart) {
        // Domain part deve ter no máximo 253 caracteres
        if (domainPart.length() > 253) {
            return false;
        }
        
        // Deve conter pelo menos um ponto
        int dotIndex = domainPart.indexOf('.');
        if (dotIndex == -1 || dotIndex == 0 || dotIndex == domainPart.length() - 1) {
            return false;
        }
        
        // Cada parte do domínio entre pontos deve ter 1-63 caracteres
        String[] parts = domainPart.split("\\.");
        for (String part : parts) {
            if (part.isEmpty() || part.length() > 63) {
                return false;
            }
            
            // Não pode começar ou terminar com hífen
            if (part.startsWith("-") || part.endsWith("-")) {
                return false;
            }
        }
        return true;
    }

    private boolean isCelular(String key) {
        return key != null && CELULAR_PATTERN.matcher(key).matches();
    }

    private boolean isChaveAleatoria(String key) {
        return key != null && CHAVE_ALEATORIA_PATTERN.matcher(key).matches();
    }

    // Getters
    public String getValor() { return valor; }
    public TipoChave getTipo() { return tipo; }
}
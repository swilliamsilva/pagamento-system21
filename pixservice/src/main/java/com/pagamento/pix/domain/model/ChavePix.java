package com.pagamento.pix.domain.model;

import java.util.regex.Pattern;

public class ChavePix {
    private final String valor;
    private final TipoChave tipo;

    public ChavePix(String valor) {
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

    public boolean validar() {
        return tipo != TipoChave.DESCONHECIDA;
    }

    // Métodos de validação (similares aos anteriores, mas sem CPF/CNPJ)
    private boolean isEmail(String key) {
        return Pattern.matches("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$", key);
    }

    private boolean isCelular(String key) {
        return Pattern.matches("^\\+[1-9]\\d{1,14}$", key);
    }

    private boolean isChaveAleatoria(String key) {
        return Pattern.matches("^[a-zA-Z0-9\\x2D\\x2E\\x40\\x5F]{1,77}$", key);
    }

    // Getters
    public String getValor() { return valor; }
    public TipoChave getTipo() { return tipo; }
}
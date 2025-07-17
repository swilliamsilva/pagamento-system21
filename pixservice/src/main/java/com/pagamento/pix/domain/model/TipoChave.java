package com.pagamento.pix.domain.model;

public enum TipoChave {
    CPF(true),
    CNPJ(true),
    EMAIL(false),
    TELEFONE(false),
    CELULAR(false),
    ALEATORIA(false),
    DESCONHECIDA(false);

    private final boolean sensitive;

    TipoChave(boolean sensitive) {
        this.sensitive = sensitive;
    }

    public boolean isSensitive() {
        return sensitive;
    }
}
package com.pagamento.boleto.domain.service;

import com.pagamento.boleto.domain.model.Boleto;

public class BoletoValidation {

    public static void validar(Boleto boleto) {
        if (boleto.getValor() == null || boleto.getValor() <= 0) {
            throw new IllegalArgumentException("Valor do boleto deve ser positivo.");
        }

        if (boleto.getDescricao() == null || boleto.getDescricao().isEmpty()) {
            throw new IllegalArgumentException("Descrição é obrigatória.");
        }
    }
}

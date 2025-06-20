package com.pagamento.boleto.domain.service;

import com.pagamento.boleto.domain.model.Boleto;

public class BoletoCalculos {

    public static void aplicarTaxas(Boleto boleto) {
        // Exemplo: aplicar uma taxa de R$ 2,50
        double valorComTaxa = boleto.getValor() + 2.50;
        // Simulando o setter
        // (em produção pode-se usar padrão builder ou mutation controlada)
        try {
            var field = boleto.getClass().getDeclaredField("valor");
            field.setAccessible(true);
            field.set(boleto, valorComTaxa);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao aplicar taxa no boleto", e);
        }
    }
}

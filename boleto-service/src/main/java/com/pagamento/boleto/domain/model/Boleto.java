/* ========================================================
# Classe: Boleto
# Módulo: boleto-service (Domínio)
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Entidade de domínio que representa um boleto.
# ======================================================== */

package com.pagamento.boleto.domain.model;

public class Boleto {

    private String id;
    private Double valor;
    private String descricao;
    private String vencimento;

    public Boleto(String id, Double valor, String descricao, String vencimento) {
        this.id = id;
        this.valor = valor;
        this.descricao = descricao;
        this.vencimento = vencimento;
    }

    // Getters e Setters (pode usar lombok futuramente)
    public String getId() {
        return id;
    }

    public Double getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getVencimento() {
        return vencimento;
    }
}

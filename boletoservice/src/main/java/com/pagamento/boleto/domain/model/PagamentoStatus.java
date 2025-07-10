package com.pagamento.boleto.domain.model;

public class PagamentoStatus {
    private String status;
    private String dataConfirmacao;

    public PagamentoStatus() {}

    public PagamentoStatus(String status, String dataConfirmacao) {
        this.status = status;
        this.dataConfirmacao = dataConfirmacao;
    }

    // Getters e Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDataConfirmacao() {
        return dataConfirmacao;
    }

    public void setDataConfirmacao(String dataConfirmacao) {
        this.dataConfirmacao = dataConfirmacao;
    }
}
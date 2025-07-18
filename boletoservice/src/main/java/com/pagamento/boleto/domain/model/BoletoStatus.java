package com.pagamento.boleto.domain.model;

public enum BoletoStatus {
    EMITIDO(true, true, true, true),
    REEMITIDO(true, true, true, true),
    VENCIDO(true, true, true, false),
    PAGO(false, false, false, false),
    CANCELADO(false, false, false, false);
	PENDING(false, false, false, false);
    private final boolean operacaoPermitida;
    private final boolean permiteReemissao;
    private final boolean permitePagamento;
    private final boolean permiteAtualizacao;

    BoletoStatus(boolean operacaoPermitida, 
                 boolean permiteReemissao, 
                 boolean permitePagamento,
                 boolean permiteAtualizacao) {
        this.operacaoPermitida = operacaoPermitida;
        this.permiteReemissao = permiteReemissao;
        this.permitePagamento = permitePagamento;
        this.permiteAtualizacao = permiteAtualizacao;
    }
    
    public boolean isOperacaoPermitida() {
        return operacaoPermitida;
    }
    
    public boolean permiteReemissao() {
        return permiteReemissao;
    }
    
    public boolean permitePagamento() {
        return permitePagamento;
    }
    
    public boolean permiteAtualizacao() {
        return permiteAtualizacao;
    }
    
    public boolean isFinal() {
        return this == PAGO || this == CANCELADO;
    }
}
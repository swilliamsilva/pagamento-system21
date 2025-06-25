package com.pagamento.boleto.domain.model;

public enum BoletoStatus {
    EMITIDO,
    PAGO,
    VENCIDO,
    CANCELADO,
    REEMITIDO, PENDENTE;

	String getId() {
		// TODO Auto-generated method stub
		return null;
	}
}
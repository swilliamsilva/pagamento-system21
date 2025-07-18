package com.pagamento.card.domain.enums;

public enum PaymentStatus {
    APPROVED,          // Pagamento aprovado
    DECLINED,          // Pagamento recusado
    FRAUD_SUSPECTED,   // Suspeita de fraude
    INSUFFICIENT_FUNDS,// Fundos insuficientes
    PROCESSING_ERROR,  // Erro no processamento
    UNSUPPORTED_CARD   // Bandeira não suportada
}
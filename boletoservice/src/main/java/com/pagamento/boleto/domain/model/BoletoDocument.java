package com.pagamento.boleto.domain.model;

import java.math.BigDecimal;

import jakarta.persistence.Id;

// BoletoDocument.java

// @Document(collection = \"boletos\")
public class BoletoDocument {
    @Id
    private String id;
    private String pagador;
    private BigDecimal valor;
    // outros campos iguais ao Boleto.java
}

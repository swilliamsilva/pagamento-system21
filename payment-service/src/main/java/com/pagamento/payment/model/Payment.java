/* ========================================================
# Classe: Payment
# Módulo: payment-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Entidade de domínio representando um pagamento.
# ======================================================== */

package com.pagamento.payment.model;

import java.time.LocalDateTime;

public class Payment {

    private String id;
    private String tipo; // PIX, BOLETO, CARTAO
    private Double valor;
    private LocalDateTime data;

    // Getters e Setters omitidos por simplicidade
}

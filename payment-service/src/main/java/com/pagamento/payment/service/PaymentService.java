/* ========================================================
# Classe: PaymentService
# Módulo: payment-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Lógica de orquestração de pagamento (simulada).
# ======================================================== */

package com.pagamento.payment.service;

import com.pagamento.common.request.PaymentRequest;
import com.pagamento.common.response.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public PaymentResponse processarPagamento(PaymentRequest request) {
        log.info("🔁 Orquestrando pagamento do tipo: {}", request.tipo());

        // Simulação do fluxo: decidir rota (boleto, pix, cartão) futuramente
        return new PaymentResponse("Pagamento processado com sucesso: " + request.tipo());
    }
}

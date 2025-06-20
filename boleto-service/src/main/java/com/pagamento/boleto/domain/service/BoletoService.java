/* ========================================================
# Classe: BoletoService
# Módulo: boleto-service (Domínio)
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Serviço de domínio responsável pela orquestração da geração de boletos.
# ======================================================== */

package com.pagamento.boleto.domain.service;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.ports.BoletoRepositoryPort;
import com.pagamento.boleto.domain.ports.AsaasGatewayPort;
import com.pagamento.boleto.domain.ports.NotificacaoPort;

public class BoletoService {

    private final BoletoRepositoryPort repository;
    private final AsaasGatewayPort asaasGateway;
    private final NotificacaoPort notificacaoPort;

    public BoletoService(BoletoRepositoryPort repository, AsaasGatewayPort asaasGateway, NotificacaoPort notificacaoPort) {
        this.repository = repository;
        this.asaasGateway = asaasGateway;
        this.notificacaoPort = notificacaoPort;
    }

    public void gerar(Boleto boleto) {
        // Validações e cálculos podem ser delegados
        BoletoCalculos.aplicarTaxas(boleto);
        BoletoValidation.validar(boleto);

        // Persistência
        repository.salvar(boleto);

        // Integração externa
        asaasGateway.registrar(boleto);

        // Notificação
        notificacaoPort.enviarNotificacao("Boleto gerado com sucesso para: " + boleto.getDescricao());
    }
}

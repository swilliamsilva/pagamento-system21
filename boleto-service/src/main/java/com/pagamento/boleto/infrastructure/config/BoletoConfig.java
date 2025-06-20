/* ========================================================
# Classe: BoletoConfig
# Módulo: boleto-service (Configuração)
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Bean de injeção do serviço de domínio com adapters.
# ======================================================== */

package com.pagamento.boleto.infrastructure.config;

import com.pagamento.boleto.domain.ports.AsaasGatewayPort;
import com.pagamento.boleto.domain.ports.BoletoRepositoryPort;
import com.pagamento.boleto.domain.ports.NotificacaoPort;
import com.pagamento.boleto.domain.service.BoletoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BoletoConfig {

    @Bean
    public BoletoService boletoService(
        BoletoRepositoryPort repository,
        AsaasGatewayPort gateway,
        NotificacaoPort notificacao
    ) {
        return new BoletoService(repository, gateway, notificacao);
    }
}

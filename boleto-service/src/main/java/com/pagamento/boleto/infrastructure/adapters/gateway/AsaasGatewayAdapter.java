/* ========================================================
# Classe: AsaasGatewayAdapter
# Módulo: boleto-service (Infraestrutura)
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Adapter que simula integração com a API Asaas.
# ======================================================== */

package com.pagamento.boleto.infrastructure.adapters.gateway;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.ports.AsaasGatewayPort;
import org.springframework.stereotype.Component;

@Component
public class AsaasGatewayAdapter implements AsaasGatewayPort {

    @Override
    public void registrar(Boleto boleto) {
        System.out.println("🌐 Integrando boleto com Asaas: " + boleto.getDescricao());
        // Aqui iria uma chamada real HTTP com WebClient ou RestTemplate
    }
}

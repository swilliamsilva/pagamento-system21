package com.pagamento.boleto.domain.ports;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoStatus;
import com.pagamento.boleto.domain.model.PagamentoStatus;

import java.math.BigDecimal;

public interface AsaasGatewayPort {

    /**
     * Envia um boleto para processamento no gateway Asaas
     * 
     * @param id Identificador único do boleto no sistema local
     * @param valor Valor do boleto
     * @return ID do pagamento no gateway Asaas
     */
    String enviarBoleto(String id, BigDecimal valor);

    /**
     * Consulta o status de um boleto no gateway Asaas e mapeia para nosso domínio
     * 
     * @param paymentId ID do pagamento no gateway Asaas
     * @return Status do boleto no nosso domínio ou null se não encontrado
     */
    BoletoStatus consultarBoleto(String paymentId);

    /**
     * Cancela um boleto no gateway Asaas
     * 
     * @param paymentId ID do pagamento no gateway Asaas
     * @throws RuntimeException em caso de falha no cancelamento
     */
    void cancelarBoleto(String paymentId);

    /**
     * Confirma o pagamento de um boleto
     * 
     * @param paymentId ID do pagamento no gateway Asaas
     * @return Status de confirmação do pagamento
     */
    PagamentoStatus confirmarPagamento(String paymentId);

	String registrarBoleto(Boleto boleto);

	String criarCobranca(BoletoRequestDTO request, String customerId);

	Object consultarCobranca(String cobrancaId);
}
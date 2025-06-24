package com.pagamento.asaas;

import com.pagamento.asaas.dto.CobrancaRequestDTO;
import com.pagamento.asaas.dto.CobrancaResponseDTO;
import com.pagamento.asaas.web.AsaasWebClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AsaasClient {
    private final AsaasWebClient webClient;

    public AsaasClient(AsaasWebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<CobrancaResponseDTO> criarCobranca(CobrancaRequestDTO request) {
        return webClient.criarCobranca(request);
    }
}
package com.pagamento.asaas.web;

import com.pagamento.asaas.config.AsaasConfig;
import com.pagamento.asaas.dto.CobrancaRequestDTO;
import com.pagamento.asaas.dto.CobrancaResponseDTO;
import com.pagamento.asaas.exception.AsaasException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AsaasWebClient {
    private final WebClient webClient;
    private final AsaasConfig config;

    public AsaasWebClient(AsaasConfig config) {
        this.config = config;
        this.webClient = WebClient.builder()
            .baseUrl(config.getBaseUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("access_token", config.getApiKey())
            .build();
    }

    public Mono<CobrancaResponseDTO> criarCobranca(CobrancaRequestDTO request) {
        return webClient.post()
            .uri("/v3/payments")
            .bodyValue(request)
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                response -> response.bodyToMono(String.class)
                    .flatMap(error -> Mono.error(new AsaasException("Erro ao criar cobrança: " + error))))
            .bodyToMono(CobrancaResponseDTO.class);
    }
}
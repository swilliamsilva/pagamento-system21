package com.pagamento.pix.infrastructure.integration;

import com.pagamento.infrastructure.integration.dto.BacenPixRequest;
import com.pagamento.infrastructure.integration.dto.BacenPixResponse;
import com.pagamento.pix.config.BacenConfig;
import com.pagamento.pix.core.ports.out.BacenPort;
import com.pagamento.pix.domain.model.Participante;
import com.pagamento.pix.domain.model.Pix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

@Component
public class BacenClient implements BacenPort {

    private static final Logger logger = LoggerFactory.getLogger(BacenClient.class);
    private static final String TRANSACOES_ENDPOINT = "/pix/v1/transacoes";
    private static final String ESTORNO_ENDPOINT = "/pix/v1/estornos";

    private final RestTemplate restTemplate;
    private final BacenConfig bacenConfig;
    private final RetryTemplate retryTemplate;

    public BacenClient(RestTemplate restTemplate, 
                      BacenConfig bacenConfig, 
                      RetryTemplate retryTemplate) {
        this.restTemplate = restTemplate;
        this.bacenConfig = bacenConfig;
        this.retryTemplate = retryTemplate;
    }

    @Override
    public String enviarTransacao(Pix pix) {
        return retryTemplate.execute(context -> {
            int tentativa = context.getRetryCount() + 1;
            logger.info("Tentativa {} de envio do PIX {}", tentativa, pix.getId());
            return enviarTransacaoParaBacen(pix);
        }, context -> {
            logger.error("Falha após {} tentativas para o PIX {}", context.getRetryCount(), pix.getId());
            throw new BacenIntegrationException("Falha após " + context.getRetryCount() + " tentativas");
        });
    }

    @Override
    public void estornarTransacao(String bacenId) {
        retryTemplate.execute(context -> {
            int tentativa = context.getRetryCount() + 1;
            logger.info("Tentativa {} de estorno BACEN: {}", tentativa, bacenId);
            estornarTransacaoNoBacen(bacenId);
            return null;
        }, context -> {
            logger.error("Falha após {} tentativas de estorno BACEN: {}", context.getRetryCount(), bacenId);
            throw new BacenIntegrationException("Falha no estorno após " + context.getRetryCount() + " tentativas");
        });
    }
    
    

    private String enviarTransacaoParaBacen(Pix pix) {
        String url = construirUrl(TRANSACOES_ENDPOINT);
        HttpEntity<BacenPixRequest> entity = criarRequestEntity(pix);
        
        logger.info("Enviando PIX para BACEN: {}", pix.getId());
        
        try {
            ResponseEntity<BacenPixResponse> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                BacenPixResponse.class
            );
            
            return processarRespostaTransacao(response, pix);
        } catch (RestClientException e) {
            tratarErroComunicacao("envio", pix.getId(), e);
            throw new BacenIntegrationException("Erro na comunicação com BACEN", e);
        }
    }

    private void estornarTransacaoNoBacen(String bacenId) {
        String url = construirUrl(ESTORNO_ENDPOINT + "/" + bacenId);
        HttpEntity<Void> entity = new HttpEntity<>(criarHeaders());
        
        logger.info("Estornando transação BACEN: {}", bacenId);
        
        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                Void.class
            );
            
            processarRespostaEstorno(response, bacenId);
        } catch (RestClientException e) {
            tratarErroComunicacao("estorno", bacenId, e);
            throw new BacenIntegrationException("Erro no estorno BACEN", e);
        }
    }

    //--- Métodos auxiliares refatorados ---//
    
    private String construirUrl(String endpoint) {
        return bacenConfig.getBacenApiUrl() + endpoint;
    }

    private HttpHeaders criarHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", bacenConfig.getBacenApiKey());
        return headers;
    }

    private HttpEntity<BacenPixRequest> criarRequestEntity(Pix pix) {
        return new HttpEntity<>(mapToBacenRequest(pix), criarHeaders());
    }

    private String processarRespostaTransacao(ResponseEntity<BacenPixResponse> response, Pix pix) {
        if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
            logger.info("PIX enviado com sucesso ao BACEN: {}", pix.getId());
            return response.getBody().getId();
        }
        logger.warn("Resposta inesperada do BACEN: {}", response.getStatusCode());
        throw new BacenIntegrationException("Resposta inesperada do BACEN: " + response.getStatusCode());
    }

    private void processarRespostaEstorno(ResponseEntity<Void> response, String bacenId) {
        if (response.getStatusCode() == HttpStatus.ACCEPTED) {
            logger.info("Estorno realizado com sucesso: {}", bacenId);
            return;
        }
        logger.warn("Resposta inesperada do BACEN para estorno: {}", response.getStatusCode());
        throw new BacenIntegrationException("Resposta inesperada: " + response.getStatusCode());
    }

    private void tratarErroComunicacao(String operacao, String id, RestClientException e) {
        String mensagem = "Erro durante {} do BACEN para {}: {}";
        
        if (e instanceof HttpClientErrorException || e instanceof HttpServerErrorException) {
            HttpStatusCode status = ((HttpStatusCodeException) e).getStatusCode();
            logger.error(mensagem, operacao, id, status);
        } else if (e instanceof ResourceAccessException) {
            logger.error(mensagem, operacao, id, e.getMessage());
        } else {
            logger.error("Erro inesperado no " + operacao + " BACEN para " + id, e);
        }
    }

    // Mantido igual por ser específico do mapeamento
    private BacenPixRequest mapToBacenRequest(Pix pix) {
        BacenPixRequest request = new BacenPixRequest();
        request.setEndToEndId(pix.getId());
        request.setValor(pix.getValor());
        request.setChave(pix.getChaveDestino().getValor());
        request.setDataHora(pix.getDataTransacao());
        
        request.setPagador(mapearParticipante(pix.getPagador()));
        request.setRecebedor(mapearParticipante(pix.getRecebedor()));
        
        return request;
    }

    private BacenPixRequest.Participante mapearParticipante(Participante participante) {
        BacenPixRequest.Participante p = new BacenPixRequest.Participante();
        p.setCpf(participante.getDocumento());
        p.setNome(participante.getNome());
        p.setIspb(participante.getIspb());
        p.setAgencia(participante.getAgencia());
        p.setConta(participante.getConta());
        return p;
    }

	public BacenPixResponse enviarPix(Pix pix) {
		// TODO Auto-generated method stub
		return null;
	}
}
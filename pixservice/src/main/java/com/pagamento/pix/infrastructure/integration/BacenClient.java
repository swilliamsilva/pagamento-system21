package com.pagamento.pix.infrastructure.integration;

import com.pagamento.pix.config.BacenConfig;
import com.pagamento.pix.core.ports.out.BacenPort;
import com.pagamento.pix.domain.model.*;
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
            return estornarTransacaoNoBacen(bacenId);
        }, context -> {
            logger.error("Falha após {} tentativas de estorno BACEN: {}", context.getRetryCount(), bacenId);
            throw new BacenIntegrationException("Falha no estorno após " + context.getRetryCount() + " tentativas");
        });
    }

    private String enviarTransacaoParaBacen(Pix pix) {
        String url = bacenConfig.getBacenApiUrl() + TRANSACOES_ENDPOINT;
        
        try {
            HttpEntity<BacenPixRequest> entity = criarRequestEntity(pix);
            
            logger.info("Enviando PIX para BACEN: {}", pix.getId());
            
            ResponseEntity<BacenPixResponse> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                BacenPixResponse.class
            );
            
            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                logger.info("PIX enviado com sucesso ao BACEN: {}", pix.getId());
                return response.getBody().getId();
            } else {
                logger.warn("Resposta inesperada do BACEN: {}", response.getStatusCode());
                throw new BacenIntegrationException("Resposta inesperada do BACEN: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Erro HTTP na comunicação com BACEN: {}", e.getStatusCode());
            throw new BacenIntegrationException("Erro BACEN: " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            logger.error("Erro de conexão com BACEN: {}", e.getMessage());
            throw new BacenIntegrationException("Erro de conexão com BACEN", e);
        } catch (Exception e) {
            logger.error("Erro inesperado na comunicação com BACEN", e);
            throw new BacenIntegrationException("Erro inesperado", e);
        }
    }

    private Void estornarTransacaoNoBacen(String bacenId) {
        String url = bacenConfig.getBacenApiUrl() + ESTORNO_ENDPOINT + "/" + bacenId;
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", bacenConfig.getBacenApiKey());
            
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            logger.info("Estornando transação BACEN: {}", bacenId);
            
            ResponseEntity<Void> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                Void.class
            );
            
            if (response.getStatusCode() == HttpStatus.ACCEPTED) {
                logger.info("Estorno realizado com sucesso: {}", bacenId);
                return null;
            } else {
                logger.warn("Resposta inesperada do BACEN para estorno: {}", response.getStatusCode());
                throw new BacenIntegrationException("Resposta inesperada: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Erro HTTP no estorno BACEN: {}", e.getStatusCode());
            throw new BacenIntegrationException("Erro estorno: " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            logger.error("Erro de conexão no estorno BACEN: {}", e.getMessage());
            throw new BacenIntegrationException("Erro de conexão", e);
        } catch (Exception e) {
            logger.error("Erro inesperado no estorno BACEN", e);
            throw new BacenIntegrationException("Erro inesperado", e);
        }
    }

    private HttpEntity<BacenPixRequest> criarRequestEntity(Pix pix) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", bacenConfig.getBacenApiKey());
        return new HttpEntity<>(mapToBacenRequest(pix), headers);
    }

    private BacenPixRequest mapToBacenRequest(Pix pix) {
        BacenPixRequest request = new BacenPixRequest();
        request.setEndToEndId(pix.getId());
        request.setValor(pix.getValor());
        request.setChave(pix.getChaveDestino().getValor());
        request.setDataHora(pix.getDataTransacao());
        
        // Mapear pagador
        BacenPixRequest.Participante pagador = new BacenPixRequest.Participante();
        pagador.setCpf(pix.getPagador().getDocumento());
        pagador.setNome(pix.getPagador().getNome());
        pagador.setIspb(pix.getPagador().getIspb());
        pagador.setAgencia(pix.getPagador().getAgencia());
        pagador.setConta(pix.getPagador().getConta());
        request.setPagador(pagador);
        
        // Mapear recebedor
        BacenPixRequest.Participante recebedor = new BacenPixRequest.Participante();
        recebedor.setCpf(pix.getRecebedor().getDocumento());
        recebedor.setNome(pix.getRecebedor().getNome());
        recebedor.setIspb(pix.getRecebedor().getIspb());
        recebedor.setAgencia(pix.getRecebedor().getAgencia());
        recebedor.setConta(pix.getRecebedor().getConta());
        request.setRecebedor(recebedor);
        
        return request;
    }
}
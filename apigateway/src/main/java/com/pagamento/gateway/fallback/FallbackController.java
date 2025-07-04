package com.pagamento.gateway.fallback;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
/**
 * Resource	Date	Description
FallbackController.java	17 hours ago	Remove this unused import 'java.util.concurrent.TimeUnit'.

 * 
 * **/


import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private static final Logger logger = LoggerFactory.getLogger(FallbackController.class);
    
    // Tempo padrão de recuperação estimada (30 segundos)
    private static final long DEFAULT_RECOVERY_SECONDS = 30;

    private static final Map<String, ServiceInfo> SERVICE_INFO = Map.ofEntries(
        Map.entry("authservice", new ServiceInfo("Serviço de Autenticação", 30)),
        Map.entry("assasintegration", new ServiceInfo("Integração com Asaas", 60)),
        Map.entry("boletoservice", new ServiceInfo("Serviço de Controle e Pagamento de Boleto", 45)),
        Map.entry("cardservice", new ServiceInfo("Serviço de Controle e Pagamento por Cartão", 45)),
        Map.entry("cloudaws", new ServiceInfo("Serviço de Administração de Cloud AWS", 120)),
        Map.entry("common", new ServiceInfo("Núcleo Comum de Aplicações", 30)),
        Map.entry("paymentservice", new ServiceInfo("Orquestrador de Pagamento", 60)),
        Map.entry("pixservice", new ServiceInfo("Serviço de Controle de Pix", 30))
    );

    @GetMapping("/{service}")
    public ResponseEntity<Map<String, Object>> serviceFallback(
            @PathVariable String service,
            @RequestHeader(value = "X-Client-Retry", defaultValue = "0") int retryCount) {
        
        // Normaliza o nome do serviço
        String serviceKey = service.toLowerCase();
        ServiceInfo serviceInfo = SERVICE_INFO.getOrDefault(serviceKey, 
                new ServiceInfo("Serviço", DEFAULT_RECOVERY_SECONDS));
        
        // Calcula tempo de recuperação baseado em tentativas
        long recoverySeconds = calculateRecoveryTime(serviceInfo.recoverySeconds, retryCount);
        Instant recoveryEstimate = Instant.now().plusSeconds(recoverySeconds);

        logger.warn("Fallback acionado para o serviço: {} (Tentativa: {})", service, retryCount);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Service Unavailable");
        response.put("message", serviceInfo.name + " está indisponível no momento. Tente novamente mais tarde.");
        response.put("timestamp", Instant.now());
        response.put("service", service);
        response.put("recovery_estimate", recoveryEstimate);
        response.put("retry_after_seconds", recoverySeconds);
        response.put("retry_count", retryCount);

        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .header("Cache-Control", "no-store, max-age=0")
            .header("Content-Security-Policy", "default-src 'none'")
            .header("Retry-After", String.valueOf(recoverySeconds))
            .header("X-Service-Status", "unavailable")
            .body(response);
    }

    private long calculateRecoveryTime(long baseSeconds, int retryCount) {
        // Aumenta o tempo de espera exponencialmente baseado nas tentativas
        if (retryCount <= 0) return baseSeconds;
        return baseSeconds * (long) Math.pow(1.5, Math.min(retryCount, 5));
    }

    // Classe interna para armazenar informações do serviço
    private static class ServiceInfo {
        final String name;
        final long recoverySeconds;

        ServiceInfo(String name, long recoverySeconds) {
            this.name = name;
            this.recoverySeconds = recoverySeconds;
        }
    }
}
package com.pagamento.gateway.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private static final Logger logger = LoggerFactory.getLogger(FallbackController.class);
    
    private static final Map<String, String> SERVICE_NAMES = Map.ofEntries(
        Map.entry("pix", "Serviço de Pagamento Pix"),
        Map.entry("auth", "Serviço de Autenticação"),
        Map.entry("assasintegration", "Integração com Assas"),
        Map.entry("authservice", "Serviço de Autenticação"),
        Map.entry("boletoservice", "Serviço de Controle e Pagamento de Boleto"),
        Map.entry("cardservice", "Serviço de Controle e Pagamento por Cartão"),
        Map.entry("cloudaws", "Serviço de Administração de Cloud AWS"),
        Map.entry("common", "Núcleo Comum de Aplicações"),
        Map.entry("paymentservice", "Orquestrador de Pagamento"),
        Map.entry("pixservice", "Serviço de Controle de Pix")
    );

    @GetMapping("/{service}")
    public ResponseEntity<Map<String, Object>> serviceFallback(@PathVariable String service) {
        String serviceName = SERVICE_NAMES.getOrDefault(service, "Serviço");
        
        logger.warn("Fallback acionado para o serviço: {}", service);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Service Unavailable");
        response.put("message", serviceName + " está indisponível no momento. Tente novamente mais tarde.");
        response.put("timestamp", Instant.now());
        response.put("service", service);
        response.put("recovery_estimate", Instant.now().plusSeconds(30)); // Estimativa de recuperação
        
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .header("Cache-Control", "no-store, max-age=0")
            .header("Content-Security-Policy", "default-src 'none'")
            .header("Retry-After", "30") // Sugere tentar novamente após 30 segundos
            .body(response);
    }
}
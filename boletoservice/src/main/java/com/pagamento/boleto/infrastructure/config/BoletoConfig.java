package com.pagamento.boleto.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pagamento.boleto.domain.model.BoletoFactory;
import com.pagamento.boleto.domain.ports.AsaasGatewayPort;
import com.pagamento.boleto.domain.ports.BoletoRepositoryPort;
import com.pagamento.boleto.domain.ports.NotificacaoPort;
import com.pagamento.boleto.domain.service.BoletoCalculos;
import com.pagamento.boleto.domain.service.BoletoService;
import com.pagamento.boleto.domain.service.BoletoValidation;

@Configuration
public class BoletoConfig {

    @Bean
    public BoletoService boletoService(
        BoletoRepositoryPort repository,
        AsaasGatewayPort asaasGateway,
        NotificacaoPort notificacaoPort,
        BoletoValidation validation,
        BoletoCalculos calculos,
        BoletoFactory factory
    ) {
        return new BoletoService(
            repository, 
            asaasGateway, 
            notificacaoPort,
            validation,
            calculos,
            factory
        );
    }
    
    @Bean
    public BoletoValidation boletoValidation() {
        return new BoletoValidation();
    }
    
    @Bean
    public BoletoCalculos boletoCalculos() {
        return new BoletoCalculos();
    }
    
    @Bean
    public BoletoFactory boletoFactory(BoletoCalculos calculos) {
        return new BoletoFactory(calculos);
    }
}
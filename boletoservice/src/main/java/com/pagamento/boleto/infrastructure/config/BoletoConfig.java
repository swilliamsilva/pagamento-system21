package com.pagamento.boleto.infrastructure.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.pagamento.boleto.domain.model.BoletoFactory;
import com.pagamento.boleto.domain.ports.AsaasGatewayPort;
import com.pagamento.boleto.domain.ports.BoletoRepositoryPort;
import com.pagamento.boleto.domain.ports.NotificacaoPort;
import com.pagamento.boleto.domain.service.BoletoCalculos;
import com.pagamento.boleto.domain.service.BoletoService;
import com.pagamento.boleto.domain.service.BoletoValidation;
import com.pagamento.boleto.domain.service.PdfService;
import com.pagamento.boleto.domain.service.TaxasService;

import javax.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
public class BoletoConfig {

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new org.springframework.orm.jpa.JpaTransactionManager(emf);
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
    public TaxasService taxasService() {
        return new TaxasService();
    }
    
    @Bean
    public PdfService pdfService() {
        return new PdfService();
    }
    
    @Bean
    public BoletoFactory boletoFactory(BoletoCalculos calculos) {
        return new BoletoFactory(calculos);
    }
    
    @Bean
    public BoletoService boletoService(
        BoletoRepositoryPort repository,
        AsaasGatewayPort asaasGateway,
        NotificacaoPort notificacaoPort,
        BoletoValidation validation,
        BoletoFactory factory,
        TaxasService taxasService,
        PdfService pdfService,
        ApplicationContext applicationContext
    ) {
        return new BoletoService(
            repository, 
            asaasGateway, 
            notificacaoPort,
            validation,
            factory,
            taxasService,
            pdfService,
            applicationContext
        );
    }
}
package com.pagamento.pix.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class BacenConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void deveCriarRestTemplateBean() {
        RestTemplate restTemplate = context.getBean(RestTemplate.class);
        assertNotNull(restTemplate);
    }

    @Test
    void deveCarregarPropriedadesCorretamente() {
        BacenConfig config = context.getBean(BacenConfig.class);
        
        assertNotNull(config.getBacenApiUrl());
        assertNotNull(config.getBacenApiKey());
        assertTrue(config.getBacenApiUrl().startsWith("http"));
        assertFalse(config.getBacenApiKey().isEmpty());
    }

    @Test
    void deveLancarExcecaoSeUrlNaoConfigurada() {
        BacenConfig config = new BacenConfig();
        config.bacenApiUrl = null;
        config.bacenApiKey = "valid-key";
        
        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            config::validate
        );
        
        assertEquals("BACEN API URL não configurada", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoSeKeyNaoConfigurada() {
        BacenConfig config = new BacenConfig();
        config.bacenApiUrl = "http://valid-url.com";
        config.bacenApiKey = "";
        
        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            config::validate
        );
        
        assertEquals("BACEN API KEY não configurada", ex.getMessage());
    }

    @Test
    void naoDeveLancarExcecaoParaConfiguracaoValida() {
        BacenConfig config = new BacenConfig();
        config.bacenApiUrl = "http://api.bacen.com";
        config.bacenApiKey = "secret-key";
        
        assertDoesNotThrow(config::validate);
    }
}
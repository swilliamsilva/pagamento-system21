package com.pagamento.gateway;

import com.pagamento.gateway.fallback.FallbackController;
import com.pagamento.gateway.filters.LoggingFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
    "spring.cloud.gateway.enabled=true",
    "spring.main.web-application-type=reactive"
})
class ContextLoadTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertNotNull(context);
    }

    @Test
    void shouldHaveLoggingFilterBean() {
        assertNotNull(context.getBean(LoggingFilter.class));
    }

    @Test
    void shouldHaveFallbackControllerBean() {
        assertNotNull(context.getBean(FallbackController.class));
    }
}

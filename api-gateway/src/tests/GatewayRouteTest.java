package tests.gateway;

import com.pagamento.gateway.GatewayApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = GatewayApplication.class)
class GatewayRouteTest {

    @Test
    void contextLoads() {
        // Teste mínimo para garantir que o contexto do gateway sobe
    }
}

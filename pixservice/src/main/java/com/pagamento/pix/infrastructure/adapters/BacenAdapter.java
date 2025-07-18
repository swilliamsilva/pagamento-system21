package com.pagamento.pix.infrastructure.adapters;

import com.pagamento.pix.core.ports.out.BacenPort;
import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.infrastructure.integration.BacenClient;
import org.springframework.stereotype.Component;

@Component
public class BacenAdapter implements BacenPort {

    private final BacenClient bacenClient;

    public BacenAdapter(BacenClient bacenClient) {
        this.bacenClient = bacenClient;
    }

    @Override
    public String enviarTransacao(Pix pix) {
        return bacenClient.enviarTransacao(pix);
    }

    @Override
    public void estornarTransacao(String bacenId) {
        bacenClient.estornarTransacao(bacenId);
    }
}
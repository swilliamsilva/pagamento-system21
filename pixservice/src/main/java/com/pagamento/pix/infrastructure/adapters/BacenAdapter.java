package com.pagamento.pix.infrastructure.adapters;

import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.core.ports.out.BacenPort;
import com.pagamento.pix.infrastructure.integration.BacenClient;
import com.pagamento.infrastructure.integration.dto.BacenPixResponse;
import org.springframework.stereotype.Component;

@Component
public class BacenAdapter implements BacenPort {

    private final BacenClient bacenClient;

    public BacenAdapter(BacenClient bacenClient) {
        this.bacenClient = bacenClient;
    }

    @Override
    public String enviarTransacao(Pix pix) {
        BacenPixResponse response = bacenClient.enviarPix(pix);
        return response.getId();
    }
}
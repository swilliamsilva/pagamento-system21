package com.pagamento.pix.core.ports.out;

import com.pagamento.pix.domain.model.Pix;
import com.pagamento.pix.infrastructure.integration.dto.BacenPixResponse;

public interface BacenPort {
    String enviarTransacao(Pix pix);
    void estornarTransacao(String bacenId);
	BacenPixResponse enviarPix(Pix pix);
}
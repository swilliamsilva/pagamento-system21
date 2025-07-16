package com.pagamento.pix.core.ports.out;

import com.pagamento.pix.domain.model.Pix;

public interface BacenPort {
    String enviarTransacao(Pix pix);
    void estornarTransacao(String bacenId);
}
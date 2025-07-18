package com.pagamento.pix.domain.ports;

import com.pagamento.pix.domain.model.Pix;

public interface PixServicePort {
    Pix processarPix(Pix pix);
}
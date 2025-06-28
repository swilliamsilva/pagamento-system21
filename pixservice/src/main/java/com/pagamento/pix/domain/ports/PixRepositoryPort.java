/* ========================================================
# Interface: PixRepositoryPort
# Módulo: pix-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Porta de saída para persistência de transações Pix.
# ======================================================== */

package com.pagamento.pix.domain.ports;

import com.pagamento.pix.model.Pix;
import java.util.Optional;

public interface PixRepositoryPort {
    Optional<Pix> findById(String id);
    Pix save(Pix pix);
    void deleteById(String id);
}

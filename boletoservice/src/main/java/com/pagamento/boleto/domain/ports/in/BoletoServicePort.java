package com.pagamento.boleto.domain.ports.in;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;
import com.pagamento.boleto.domain.model.Boleto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BoletoServicePort {
    Boleto emitirBoleto(BoletoRequestDTO dto);
    Boleto reemitirBoleto(String idOriginal);
    Boleto cancelarBoleto(String id, String motivo);
    Boleto consultarBoleto(String id);
    Page<Boleto> listarBoletos(Pageable pageable);
    byte[] gerarPDF(String id);
    String gerarCodigoBarras(String id);
    String gerarQRCode(String id);
    UUID gerarBoleto(com.pagamento.common.dto.BoletoRequestDTO request);
}



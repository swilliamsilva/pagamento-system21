package com.pagamento.boleto.domain.ports;

import org.springframework.data.domain.Pageable;

import com.pagamento.boleto.application.dto.BoletoRequestDTO;


import com.pagamento.boleto.domain.model.Boleto;
public interface BoletoServicePort {
    Boleto emitirBoleto(BoletoRequestDTO dto);
    Boleto consultarBoleto(String id);
    Boleto cancelarBoleto(String id);
    Boleto reemitirBoleto(String id);
    byte[] gerarPDF(String id);
    String gerarCodigoBarras(String id);
    String gerarQRCode(String id);
	void cancelarBoleto(String id, String motivo);
	Object listarBoletos(Pageable any);
}
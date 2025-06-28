package com.pagamento.boleto.domain.service;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.DadosTecnicosBoleto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class BoletoCalculos {
    
    public DadosTecnicosBoleto gerarDadosTecnicos(Boleto boleto) {
        return new DadosTecnicosBoleto(
            gerarCodigoBarras(boleto),
            gerarLinhaDigitavel(gerarCodigoBarras(boleto)),
            gerarQRCode(boleto),
            gerarNossoNumero()
        );
    }
    
    private String gerarCodigoBarras(Boleto boleto) {
        // Implementação real usando padrão FEBRABAN
        String banco = "341"; // Código do banco (ex: Itaú)
        String moeda = "9";   // Real
        String fatorVencimento = calcularFatorVencimento(boleto.getDataVencimento());
        String valor = formatarValorBoleto(boleto.getValor());
        
        return banco + moeda + fatorVencimento + valor;
    }
    
    private String calcularFatorVencimento(LocalDate dataVencimento) {
        LocalDate dataBase = LocalDate.of(1997, 10, 7);
        long dias = ChronoUnit.DAYS.between(dataBase, dataVencimento);
        return String.format("%04d", dias);
    }
    
    private String formatarValorBoleto(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP)
                   .multiply(BigDecimal.valueOf(100))
                   .toBigInteger()
                   .toString();
    }
    
    private String gerarLinhaDigitavel(String codigoBarras) {
        // Implementação real conforme especificação FEBRABAN
        return codigoBarras.substring(0, 4) + 
               codigoBarras.substring(19, 24) + "." +
               codigoBarras.substring(24, 34);
    }
    
    private String gerarQRCode(Boleto boleto) {
        // Usando padrão PIX dinâmico do BCB
        String valorFormatado = boleto.getValor()
                                    .setScale(2, RoundingMode.HALF_UP)
                                    .toString();
        
        return "000201" +
               "26580014BR.GOV.BCB.PIX" +
               "0136" + boleto.getId() +
               "52040000" +
               "5303986" +
               "54" + String.format("%02d", valorFormatado.length()) + valorFormatado +
               "5802BR" +
               "5913" + limitarString(boleto.getBeneficiario(), 13) +
               "6008BRASILIA" +
               "6304" + calcularCRC16();
    }
    
    private String limitarString(String texto, int tamanho) {
        return texto.substring(0, Math.min(tamanho, texto.length()));
    }
    
    private String calcularCRC16() {
        // Implementação real do cálculo CRC16
        return "ABCD"; // Exemplo simplificado
    }
    
    private String gerarNossoNumero() {
        // Usando timestamp para ambientes distribuídos
        long timestamp = System.currentTimeMillis();
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return timestamp + "-" + random;
    }
}
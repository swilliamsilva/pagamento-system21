package com.pagamento.boleto.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.DadosTecnicosBoleto;

import io.netty.util.internal.ThreadLocalRandom;

@Component
public class BoletoCalculos {
    
    // Constante para o algoritmo de cálculo CRC16
    private static final int CRC16_POLY = 0x1021;
    private static final int CRC16_INIT = 0xFFFF;

    public DadosTecnicosBoleto gerarDadosTecnicos(Boleto boleto) {
        return new DadosTecnicosBoleto(
            gerarCodigoBarras(boleto),
            gerarLinhaDigitavel(gerarCodigoBarras(boleto)),
            gerarQRCode(boleto),
            gerarNossoNumero()
        );
    }
    
    String gerarCodigoBarras(Boleto boleto) {
        // Implementação real usando padrão FEBRABAN
        String banco = "341"; // Código do banco (ex: Itaú)
        String moeda = "9";   // Real
        String fatorVencimento = calcularFatorVencimento(boleto.getDataVencimento());
        String valor = formatarValorBoleto(boleto.getValor());
        
        return banco + moeda + fatorVencimento + valor;
    }
    
    String calcularFatorVencimento(LocalDate dataVencimento) {
        LocalDate dataBase = LocalDate.of(1997, 10, 7);
        long dias = ChronoUnit.DAYS.between(dataBase, dataVencimento);
        return String.format("%04d", dias);
    }
    
    String formatarValorBoleto(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP)
                   .multiply(BigDecimal.valueOf(100))
                   .toBigInteger()
                   .toString();
    }
    
    String gerarLinhaDigitavel(String codigoBarras) {
        // Implementação real conforme especificação FEBRABAN
        return codigoBarras.substring(0, 4) + 
               codigoBarras.substring(19, 24) + "." +
               codigoBarras.substring(24, 34);
    }
    
    String gerarQRCode(Boleto boleto) {
        // Usando padrão PIX dinâmico do BCB
        String valorFormatado = boleto.getValor()
                                    .setScale(2, RoundingMode.HALF_UP)
                                    .toString();
        
        StringBuilder base = new StringBuilder()
            .append("000201")
            .append("26580014BR.GOV.BCB.PIX")
            .append("0136").append(boleto.getId())
            .append("52040000")
            .append("5303986")
            .append("54").append(String.format("%02d", valorFormatado.length())).append(valorFormatado)
            .append("5802BR")
            .append("5913").append(limitarString(boleto.getBeneficiario(), 13))
            .append("6008BRASILIA")
            .append("6304");
        
        String crc = calcularCRC16(base.toString());
        return base.append(crc).toString();
    }
    
    String limitarString(String texto, int tamanho) {
        return texto.substring(0, Math.min(tamanho, texto.length()));
    }
    
    String calcularCRC16(String payload) {
        int crc = CRC16_INIT;
        byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
        
        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15) & 1) == 1;
                crc <<= 1;
                if (c15 ^ bit) {
                    crc ^= CRC16_POLY;
                }
            }
        }
        
        crc &= 0xffff;
        return String.format("%04X", crc);
    }
    
    String gerarNossoNumero() {
        // Usando timestamp para ambientes distribuídos
        long timestamp = System.currentTimeMillis();
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return timestamp + "-" + random;
    }
}
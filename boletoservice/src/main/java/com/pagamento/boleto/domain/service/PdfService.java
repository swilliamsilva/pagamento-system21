package com.pagamento.boleto.domain.service;

import com.pagamento.boleto.domain.exception.PdfGenerationException;
import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.DadosTecnicosBoleto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public byte[] gerarPdf(Boleto boleto) {
        return gerarPdfComDadosBoleto(boleto);
    }

    private byte[] gerarPdfComDadosBoleto(Boleto boleto) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Configurações iniciais
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("BOLETO BANCÁRIO");
                contentStream.endText();
                
                // Dados principais
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Pagador: " + boleto.getPagador());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Beneficiário: " + boleto.getBeneficiario());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Valor: " + formatarMoeda(boleto.getValor()));
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Vencimento: " + formatarData(boleto.getDataVencimento()));
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Emissão: " + formatarData(boleto.getDataEmissao()));
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Documento: " + boleto.getDocumento());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Instruções: " + boleto.getInstrucoes());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Local Pagamento: " + boleto.getLocalPagamento());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Status: " + boleto.getStatus());
                contentStream.endText();
                
                // Dados técnicos
                DadosTecnicosBoleto dadosTecnicos = boleto.getDadosTecnicos();
                if (dadosTecnicos != null) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, 550);
                    contentStream.showText("Código de Barras: " + dadosTecnicos.getCodigoBarras());
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("Linha Digitável: " + dadosTecnicos.getLinhaDigitavel());
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("QR Code: " + dadosTecnicos.getQrCode());
                    contentStream.endText();
                }
            }
            
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new PdfGenerationException("Falha ao gerar PDF do boleto", e);
        }
    }
    
    private String formatarMoeda(BigDecimal valor) {
        return "R$ " + valor.setScale(2, RoundingMode.HALF_UP);
    }
    
    private String formatarData(LocalDate data) {
        return data != null ? data.format(DATE_FORMATTER) : "N/A";
    }
}
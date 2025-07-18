package com.pagamento.boleto.application.mapper;

import com.pagamento.boleto.domain.model.Boleto;
import com.pagamento.boleto.domain.model.BoletoDocument;
import com.pagamento.boleto.domain.model.DadosTecnicosBoleto;

/**
 * Mapper para conversão entre a entidade Boleto e o documento BoletoDocument
 */
public final class BoletoDocumentMapper {

    // Construtor privado para prevenir instanciação
    private BoletoDocumentMapper() {
        throw new AssertionError("Classe utilitária não deve ser instanciada");
    }

    /**
     * Converte a entidade Boleto para BoletoDocument
     * @param boleto Entidade de domínio
     * @return Documento para persistência
     */
    public static BoletoDocument toDocument(Boleto boleto) {
        if (boleto == null) return null;
        
        BoletoDocument doc = new BoletoDocument();
        doc.setId(boleto.getId().toString());
        doc.setPagador(boleto.getPagador());
        doc.setBeneficiario(boleto.getBeneficiario());
        doc.setValor(boleto.getValor());
        doc.setDataVencimento(boleto.getDataVencimento());
        doc.setDataEmissao(boleto.getDataEmissao());
        doc.setStatus(boleto.getStatus());
        doc.setDocumento(boleto.getDocumento());
        doc.setInstrucoes(boleto.getInstrucoes());
        doc.setLocalPagamento(boleto.getLocalPagamento());
        doc.setMotivoCancelamento(boleto.getMotivoCancelamento());
        doc.setNumeroReemissoes(boleto.getNumeroReemissoes());
        doc.setBoletoOriginalId(boleto.getBoletoOriginalId());
        doc.setIdExterno(boleto.getIdExterno());
        
        if (boleto.getDadosTecnicos() != null) {
            BoletoDocument.DadosTecnicosDocument tecnicos = new BoletoDocument.DadosTecnicosDocument(
                boleto.getDadosTecnicos().getCodigoBarras(),
                boleto.getDadosTecnicos().getQrCode(),
                boleto.getDadosTecnicos().getLinhaDigitavel()
            );
            doc.setDadosTecnicos(tecnicos);
        }
        
        return doc;
    }

    /**
     * Converte o documento BoletoDocument para entidade Boleto
     * @param doc Documento persistido
     * @return Entidade de domínio
     */
    public static Boleto toEntity(BoletoDocument doc) {
        if (doc == null) return null;
        
        Boleto boleto = new Boleto();
        boleto.setId(UUID.fromString(doc.getId()));
        boleto.setPagador(doc.getPagador());
        boleto.setBeneficiario(doc.getBeneficiario());
        boleto.setValor(doc.getValor());
        boleto.setDataVencimento(doc.getDataVencimento());
        boleto.setDataEmissao(doc.getDataEmissao());
        boleto.setStatus(doc.getStatus());
        boleto.setDocumento(doc.getDocumento());
        boleto.setInstrucoes(doc.getInstrucoes());
        boleto.setLocalPagamento(doc.getLocalPagamento());
        boleto.setMotivoCancelamento(doc.getMotivoCancelamento());
        boleto.setNumeroReemissoes(doc.getNumeroReemissoes());
        boleto.setBoletoOriginalId(doc.getBoletoOriginalId());
        boleto.setIdExterno(doc.getIdExterno());
        
        if (doc.getDadosTecnicos() != null) {
            DadosTecnicosBoleto tecnicos = new DadosTecnicosBoleto(
                doc.getDadosTecnicos().getCodigoBarras(),
                doc.getDadosTecnicos().getLinhaDigitavel(),
                doc.getDadosTecnicos().getQrCode(),
                doc.getDadosTecnicos().getNossoNumero()
            );
            boleto.setDadosTecnicos(tecnicos);
        }
        
        return boleto;
    }
}
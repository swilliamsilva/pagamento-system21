package com.pagamento.pix.domain.service;

import com.pagamento.pix.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PixValidatorTest {

    private PixValidator validator;
    private Pix pix;

    @BeforeEach
    void setUp() {
        validator = new PixValidator();
        pix = criarPixValido();
    }

    private Pix criarPixValido() {
        Participante pagador = criarParticipanteValido(
            "João Silva", 
            "52998224725", 
            "12345678", 
            "0001", 
            "12345-6"
        );

        Participante recebedor = criarParticipanteValido(
            "Empresa XYZ", 
            "11222333000144", 
            "87654321", 
            "0002", 
            "65432-1"
        );

        return Pix.builder()
                .id("PIX-12345")
                .chaveOrigem(new ChavePix("12345678909"))
                .chaveDestino(new ChavePix("recebedor@empresa.com"))
                .valor(new BigDecimal("150.99"))
                .dataTransacao(LocalDateTime.now())
                .taxa(0.99)
                .pagador(pagador)
                .recebedor(recebedor)
                .build();
    }

    private Participante criarParticipanteValido(String nome, String documento, 
                                                String ispb, String agencia, String conta) {
        Participante participante = new Participante();
        participante.setNome(nome);
        participante.setDocumento(documento);
        participante.setIspb(ispb);
        participante.setAgencia(agencia);
        participante.setConta(conta);
        return participante;
    }

    @Test
    void deveValidarPixCorreto() {
        assertTrue(validator.validar(pix));
    }

    @Test
    void deveRejeitarQuandoValorInvalido() {
        Pix pixInvalido = Pix.builder()
                .id("PIX-56789")
                .chaveOrigem(new ChavePix("12345678909"))
                .chaveDestino(new ChavePix("outro@email.com"))
                .valor(BigDecimal.ZERO)  // Valor inválido
                .pagador(pix.getPagador())
                .recebedor(pix.getRecebedor())
                .build();
        
        assertFalse(validator.validar(pixInvalido));
    }

    @Test
    void deveRejeitarQuandoChavesIguais() {
        Pix pixInvalido = Pix.builder()
                .from(pix)  // Copia atributos válidos
                .chaveDestino(new ChavePix("12345678909"))  // Igual à origem
                .build();
        
        assertFalse(validator.validar(pixInvalido));
    }

    @Test
    void deveRejeitarQuandoChaveOrigemInvalida() {
        Pix pixInvalido = Pix.builder()
                .from(pix)
                .chaveOrigem(new ChavePix("chave-invalida@"))
                .build();
        
        assertFalse(validator.validar(pixInvalido));
    }

    @Test
    void deveRejeitarQuandoDocumentoPagadorInvalido() {
        Participante pagadorInvalido = criarParticipanteValido(
            "Pagador Inválido", 
            "11111111111",  // CPF inválido
            pix.getPagador().getIspb(),
            pix.getPagador().getAgencia(),
            pix.getPagador().getConta()
        );

        Pix pixInvalido = Pix.builder()
                .from(pix)
                .pagador(pagadorInvalido)
                .build();
        
        assertFalse(validator.validar(pixInvalido));
    }

    @Test
    void deveRejeitarQuandoDocumentoRecebedorInvalido() {
        Participante recebedorInvalido = criarParticipanteValido(
            "Recebedor Inválido", 
            "99999999000199",  // CNPJ inválido
            pix.getRecebedor().getIspb(),
            pix.getRecebedor().getAgencia(),
            pix.getRecebedor().getConta()
        );

        Pix pixInvalido = Pix.builder()
                .from(pix)
                .recebedor(recebedorInvalido)
                .build();
        
        assertFalse(validator.validar(pixInvalido));
    }

    @Test
    void deveRejeitarQuandoFaltarParticipante() {
        Pix semPagador = Pix.builder()
                .from(pix)
                .pagador(null)
                .build();
        
        assertFalse(validator.validar(semPagador));
        
        Pix semRecebedor = Pix.builder()
                .from(pix)
                .recebedor(null)
                .build();
        
        assertFalse(validator.validar(semRecebedor));
    }

    @Test
    void deveRejeitarQuandoChaveDestinoInvalida() {
        Pix pixInvalido = Pix.builder()
                .from(pix)
                .chaveDestino(new ChavePix("+123"))  // Formato inválido
                .build();
        
        assertFalse(validator.validar(pixInvalido));
    }
}
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
        Pix pix = new Pix();
        pix.setId("PIX-12345");
        pix.setChaveOrigem(new ChavePix("12345678909")); // CPF
        pix.setChaveDestino(new ChavePix("recebedor@empresa.com"));
        pix.setValor(new BigDecimal("150.99"));
        pix.setDataTransacao(LocalDateTime.now());
        pix.setTaxa(0.99);

        Participante pagador = new Participante();
        pagador.setNome("João Silva");
        pagador.setDocumento("52998224725"); // CPF válido
        pagador.setIspb("12345678");
        pagador.setAgencia("0001");
        pagador.setConta("12345-6");
        pix.setPagador(pagador);

        Participante recebedor = new Participante();
        recebedor.setNome("Empresa XYZ");
        recebedor.setDocumento("11222333000144"); // CNPJ válido
        recebedor.setIspb("87654321");
        recebedor.setAgencia("0002");
        recebedor.setConta("65432-1");
        pix.setRecebedor(recebedor);

        return pix;
    }

    @Test
    void deveValidarPixCorreto() {
        assertTrue(validator.validar(pix));
    }

    @Test
    void deveRejeitarQuandoValorInvalido() {
        pix.setValor(BigDecimal.ZERO);
        assertFalse(validator.validar(pix));
        
        pix.setValor(new BigDecimal("-10"));
        assertFalse(validator.validar(pix));
        
        pix.setValor(null);
        assertFalse(validator.validar(pix));
    }

    @Test
    void deveRejeitarQuandoChavesIguais() {
        pix.setChaveDestino(new ChavePix("12345678909"));
        assertFalse(validator.validar(pix));
    }

    @Test
    void deveRejeitarQuandoChaveOrigemInvalida() {
        pix.setChaveOrigem(new ChavePix("chave-invalida@"));
        assertFalse(validator.validar(pix));
    }

    @Test
    void deveRejeitarQuandoDocumentoPagadorInvalido() {
        pix.getPagador().setDocumento("11111111111"); // CPF inválido
        assertFalse(validator.validar(pix));
    }

    @Test
    void deveRejeitarQuandoDocumentoRecebedorInvalido() {
        pix.getRecebedor().setDocumento("99999999000199"); // CNPJ inválido
        assertFalse(validator.validar(pix));
    }

    @Test
    void deveRejeitarQuandoFaltarParticipante() {
        pix.setPagador(null);
        assertFalse(validator.validar(pix));
        
        pix.setRecebedor(null);
        assertFalse(validator.validar(pix));
    }

    @Test
    void deveRejeitarQuandoChaveDestinoInvalida() {
        pix.setChaveDestino(new ChavePix("+123")); // Formato de celular inválido
        assertFalse(validator.validar(pix));
    }
}
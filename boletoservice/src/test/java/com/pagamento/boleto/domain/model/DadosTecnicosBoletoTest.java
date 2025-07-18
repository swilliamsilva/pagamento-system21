package com.pagamento.boleto.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DadosTecnicosBoletoTest {

    @Test
    void deveCriarComDadosValidos() {
        DadosTecnicosBoleto dados = new DadosTecnicosBoleto(
            "1234567890123456789012345678901234567890",
            "12345.67890 12345.678901 12345.678901 1 12345678901234",
            "https://qr.com/payment",
            "123456789012345"
        );

        assertThat(dados.getCodigoBarras()).isNotNull();
        assertThat(dados.getLinhaDigitavel()).isNotNull();
        assertThat(dados.getQrCode()).isNotNull();
        assertThat(dados.getNossoNumero()).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void deveLancarExcecaoParaCampoVazio(String valorInvalido) {
        assertAll(
            () -> assertThatThrownBy(() -> new DadosTecnicosBoleto(valorInvalido, "valid", "valid", "valid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Código de barras não pode ser vazio"),
            
            () -> assertThatThrownBy(() -> new DadosTecnicosBoleto("valid", valorInvalido, "valid", "valid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Linha digitável não pode ser vazia"),
            
            () -> assertThatThrownBy(() -> new DadosTecnicosBoleto("valid", "valid", valorInvalido, "valid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("QR Code não pode ser vazio"),
            
            () -> assertThatThrownBy(() -> new DadosTecnicosBoleto("valid", "valid", "valid", valorInvalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nosso número não pode ser vazio")
        );
    }

    @Test
    void deveSerIgualQuandoDadosIguais() {
        DadosTecnicosBoleto dados1 = new DadosTecnicosBoleto("123", "456", "789", "012");
        DadosTecnicosBoleto dados2 = new DadosTecnicosBoleto("123", "456", "789", "012");
        
        assertThat(dados1).isEqualTo(dados2);
        assertThat(dados1.hashCode()).isEqualTo(dados2.hashCode());
    }

    @Test
    void deveSerDiferenteQuandoDadosDiferentes() {
        DadosTecnicosBoleto dados1 = new DadosTecnicosBoleto("123", "456", "789", "012");
        DadosTecnicosBoleto dados2 = new DadosTecnicosBoleto("999", "456", "789", "012");
        
        assertThat(dados1).isNotEqualTo(dados2);
        assertThat(dados1.hashCode()).isNotEqualTo(dados2.hashCode());
    }

    @Test
    void deveAtualizarNossoNumeroCorretamente() {
        DadosTecnicosBoleto dados = new DadosTecnicosBoleto();
        dados.setNossoNumero("NOVO123");
        
        assertThat(dados.getNossoNumero()).isEqualTo("NOVO123");
        assertThatThrownBy(() -> dados.setNossoNumero(null))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
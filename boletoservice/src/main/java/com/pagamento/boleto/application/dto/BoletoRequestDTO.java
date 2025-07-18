package com.pagamento.boleto.application.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record BoletoRequestDTO(
    @NotBlank(message = "Pagador é obrigatório")
    String pagador,
    
    @NotBlank(message = "Beneficiário é obrigatório")
    String beneficiario,
    
    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    BigDecimal valor,
    
    @NotNull(message = "Data de vencimento é obrigatória")
    @Future(message = "Data de vencimento deve ser futura")
    LocalDate dataVencimento,
    
    String documento,
    String instrucoes,
    String localPagamento
) {
    // Construtor compacto para validações adicionais
    public BoletoRequestDTO {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }
    }
    
    // Construtor auxiliar simplificado
    public BoletoRequestDTO(String pagador, String beneficiario, BigDecimal valor, LocalDate dataVencimento) {
        this(pagador, beneficiario, valor, dataVencimento, null, null, null);
    }
    
    // Método para obter data de emissão com padrão
    public LocalDate getDataEmissaoOrDefault() {
        return LocalDate.now();
    }
}
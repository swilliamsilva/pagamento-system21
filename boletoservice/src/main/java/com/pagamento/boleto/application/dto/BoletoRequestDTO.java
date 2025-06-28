package com.pagamento.boleto.application.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record BoletoRequestDTO(
    @NotBlank(message = "Pagador é obrigatório")
    String pagador,
    
    @NotBlank(message = "Beneficiário é obrigatório")
    String beneficiario,
    
    @Positive(message = "Valor deve ser positivo")
    BigDecimal valor,
    
    @Future(message = "Data de vencimento deve ser futura")
    @NotNull(message = "Data de vencimento é obrigatória")
    LocalDate dataVencimento,
    
    LocalDate dataEmissao, // Opcional: default é data atual
    
    String documento,       // CPF/CNPJ do pagador
    String instrucoes,      // Instruções de pagamento
    String localPagamento   // Local preferencial de pagamento
) {
    // Construtor auxiliar para campos obrigatórios
    public BoletoRequestDTO(String pagador, String beneficiario, BigDecimal valor, LocalDate dataVencimento) {
        this(pagador, beneficiario, valor, dataVencimento, null, null, null, null);
    }
    
    // Construtor auxiliar para teste simplificado
    public BoletoRequestDTO(String pagador, LocalDate dataVencimento) {
        this(pagador, "Beneficiário Padrão", BigDecimal.valueOf(100.0), dataVencimento);
    }

    // Método para obter data de emissão com padrão
    public LocalDate getDataEmissaoOrDefault() {
        return dataEmissao != null ? dataEmissao : LocalDate.now();
    }
}
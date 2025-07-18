package com.pagamento.core.common.dto;

public record ErrorResponse(
    String code,
    String message,
    long timestamp
) {}
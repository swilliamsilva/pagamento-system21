
// ==========================
// DTO: AuthRequest.java
// ==========================
package com.pagamento.common.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Requisição de autenticação com credenciais básicas.
 */
public record AuthRequest(
    @NotBlank String username,
    @NotBlank String password
) {}

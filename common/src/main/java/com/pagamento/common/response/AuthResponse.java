
// ==========================
// DTO: AuthResponse.java
// ==========================
package com.pagamento.common.response;

/**
 * Resposta da API de autenticação com token e perfil de acesso.
 *
 * @param token Token JWT no formato "Bearer ..."
 * @param role Perfil do usuário autenticado
 */
public record AuthResponse(
    String token,
    String role
) {}

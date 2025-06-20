/* ========================================================
# Classe: AuthResponse
# Módulo: common - response
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Representa a resposta de autenticação.
# ======================================================== */

package com.pagamento.common.response;

/**
 * Retorno padrão de autenticação contendo o token JWT e o perfil do usuário.
 *
 * Exemplo de resposta:
 * {
 *   "token": "Bearer eyJhbGciOiJIUzI1NiIsIn...",
 *   "role": "admin"
 * }
 */
public record AuthResponse(
    String token,
    String role
) {}

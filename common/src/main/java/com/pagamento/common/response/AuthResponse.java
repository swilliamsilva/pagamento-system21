/* ========================================================
# Classe: AuthResponse
# Módulo: common - response
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Retorno com token após autenticação com JWT.
# ======================================================== */

package com.pagamento.common.response;

/**
 * Retorno padrão de autenticação contendo o token JWT.
 *
 * Exemplo de resposta:
 * {
 *   "token": "Bearer eyJhbGciOiJIUzI1NiIsIn..."
 * }
 */
public record AuthResponse(String token) {}

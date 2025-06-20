/* ========================================================
# Classe: UserDTO
# Módulo: common
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: DTO para dados de usuário.
# ======================================================== */

package com.pagamento.common.dto;

public record UserDTO(
    String id,
    String nome,
    String email,
    String documento
) {}

// ==========================
// DTO: UserDTO.java (Atualizado)
// ==========================
package com.pagamento.common.dto;

import com.pagamento.common.validation.ValidCPF;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDTO(
    @NotBlank(message = "ID não pode ser vazio")
    String id,

    @NotBlank(message = "Nome não pode ser vazio")
    String nome,

    @Email(message = "E-mail inválido")
    String email,

    @ValidCPF
    String documento
) {}


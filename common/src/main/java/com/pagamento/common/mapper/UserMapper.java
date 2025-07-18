package com.pagamento.common.mapper;

import com.pagamento.common.dto.UserDTO;
import com.pagamento.common.model.User;

/**
 * Converte entidades User ↔ DTO.
 * Classe utilitária - não deve ser instanciada.
 */
public class UserMapper {

    private UserMapper() {
        throw new UnsupportedOperationException("Esta é uma classe utilitária e não pode ser instanciada");
    }

    public static UserDTO toDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getNome(),
            user.getEmail(),
            user.getDocumento()
        );
    }

    public static User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.id());
        user.setNome(dto.nome());
        user.setEmail(dto.email());
        user.setDocumento(dto.documento());
        return user;
    }
}
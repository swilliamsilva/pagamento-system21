package com.pagamento.common.mapper;

import com.pagamento.common.dto.UserDTO;
import com.pagamento.common.model.User;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .nome(user.getNome())
                .email(user.getEmail())
                .documento(user.getDocumento())
                .build();
    }

    public static User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setNome(dto.getNome());
        user.setEmail(dto.getEmail());
        user.setDocumento(dto.getDocumento());
        return user;
    }
}



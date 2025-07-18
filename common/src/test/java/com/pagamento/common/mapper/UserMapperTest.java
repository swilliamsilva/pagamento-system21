// ==========================
// TEST: UserMapperTest.java
// ==========================
package com.pagamento.common.mapper;

import com.pagamento.common.dto.UserDTO;
import com.pagamento.common.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest { // Removed 'public' modifier from class

    @Test
    void deveConverterUserParaDTO() { // Removed 'public' modifier
        User user = new User();
        user.setId("001");
        user.setNome("Maria");
        user.setEmail("maria@email.com");
        user.setDocumento("12345678909");

        UserDTO dto = UserMapper.toDTO(user);
        assertEquals("001", dto.id());
        assertEquals("Maria", dto.nome());
        assertEquals("maria@email.com", dto.email());
        assertEquals("12345678909", dto.documento());
    }

    @Test
    void deveConverterDTOParaUser() { // Removed 'public' modifier
        UserDTO dto = new UserDTO("002", "Carlos", "carlos@email.com", "12345678909");
        User user = UserMapper.toEntity(dto);

        assertEquals("002", user.getId());
        assertEquals("Carlos", user.getNome());
        assertEquals("carlos@email.com", user.getEmail());
        assertEquals("12345678909", user.getDocumento());
    }
}
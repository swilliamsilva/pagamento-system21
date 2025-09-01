package com.pagamento.common.mapper;

import com.pagamento.common.dto.UserDTO;
import com.pagamento.common.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    @Test
    public void deveConverterUserParaDTO() {
        User user = new User();
        user.setId("001");
        user.setNome("Maria");
        user.setEmail("maria@email.com");
        user.setDocumento("12345678909");

        UserDTO dto = UserMapper.toDTO(user);
        assertEquals("001", dto.getId());
        assertEquals("Maria", dto.getNome());
        assertEquals("maria@email.com", dto.getEmail());
        assertEquals("12345678909", dto.getDocumento());
    }

    @Test
    public void deveConverterDTOParaUser() {
        UserDTO dto = UserDTO.builder()
                .id("002")
                .nome("Carlos")
                .email("carlos@email.com")
                .documento("12345678909")
                .build();
        User user = UserMapper.toEntity(dto);

        assertEquals("002", user.getId());
        assertEquals("Carlos", user.getNome());
        assertEquals("carlos@email.com", user.getEmail());
        assertEquals("12345678909", user.getDocumento());
    }
}



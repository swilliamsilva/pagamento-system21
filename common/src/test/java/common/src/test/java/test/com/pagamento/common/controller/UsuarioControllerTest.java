// ==========================
// TEST: UsuarioControllerTest.java
// ==========================

package common.src.test.java.test.com.pagamento.common.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagamento.common.controller.UsuarioController;
import com.pagamento.common.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {  // Removed 'public' modifier

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveAceitarUsuarioValido() throws Exception {  // Removed 'public' modifier
        UserDTO dto = new UserDTO("321", "Ana", "ana@email.com", "12345678909");

        mockMvc.perform(post("/api/usuarios")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuário válido"));
    }

    @Test
    void deveRejeitarUsuarioComEmailInvalido() throws Exception {  // Removed 'public' modifier
        UserDTO dto = new UserDTO("321", "Ana", "email-invalido", "12345678909");

        mockMvc.perform(post("/api/usuarios")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
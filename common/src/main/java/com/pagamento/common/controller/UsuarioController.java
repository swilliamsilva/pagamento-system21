// ==========================
// CONTROLLER: UsuarioController.java
// ==========================
package com.pagamento.common.controller;

import com.pagamento.common.dto.UserDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @PostMapping
    public ResponseEntity<String> cadastrarUsuario(@RequestBody @Valid UserDTO userDTO) {
        return ResponseEntity.ok("Usuário válido");
    }
}

package com.pagamento.auth.controller;

import com.pagamento.auth.security.JwtTokenProvider;
import com.pagamento.common.request.AuthRequest;
import com.pagamento.common.response.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // Simula validação de usuário
        if ("admin".equals(request.username()) && "senha123".equals(request.password())) {
            String token = jwtTokenProvider.generateToken(request.username());
            return ResponseEntity.ok(new AuthResponse("Bearer " + token));
        }
        return ResponseEntity.status(401).build();
    }
}

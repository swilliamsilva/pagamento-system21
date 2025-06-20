/* ========================================================
# Classe: AuthService
# Módulo: auth-service
# Projeto: pagamento-system21
# Autor: William Silva
# Descrição: Serviço de autenticação com geração de JWT.
# ======================================================== */

package com.pagamento.auth.service;

import com.pagamento.auth.security.JwtTokenProvider;
import com.pagamento.common.request.AuthRequest;
import com.pagamento.common.response.AuthResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponse authenticate(AuthRequest request) {
        // Em produção: aqui deve consultar banco ou userDetailsService
        if ("admin".equals(request.username()) && "123456".equals(request.password())) {
            String token = jwtTokenProvider.generateToken(request.username());
            return new AuthResponse(token, "admin");
        }

        throw new RuntimeException("Credenciais inválidas");
    }
}

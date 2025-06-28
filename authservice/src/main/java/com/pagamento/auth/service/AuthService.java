package com.pagamento.auth.service;

import com.pagamento.auth.dto.AuthRequestDTO;
import com.pagamento.auth.dto.AuthResponseDTO;
import com.pagamento.auth.model.User;
import com.pagamento.auth.repository.UserRepository;
import com.pagamento.auth.security.JwtTokenProvider;
import com.pagamento.common.request.AuthRequest;
import com.pagamento.common.response.AuthResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, 
                      JwtTokenProvider jwtTokenProvider,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

   

	public AuthResponseDTO authenticate(AuthRequestDTO request) {
        User user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Credenciais inválidas");
        }
        
        String token = jwtTokenProvider.generateToken(user.getUsername());
        return new AuthResponseDTO("Bearer " + token, user.getRole());
    }

	public AuthResponse autenticar(AuthRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
}
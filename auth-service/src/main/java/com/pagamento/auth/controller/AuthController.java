package com.pagamento.auth.controller;

import com.pagamento.auth.security.JwtTokenProvider;
import com.pagamento.common.request.AuthRequest;
import com.pagamento.common.response.AuthResponse;

/**
 * Multiple markers at this line
	- The import com.pagamento.common cannot be resolved
	- The import com.pagamento.common.request cannot be resolved
 * 
 * **/

import org.springframework.http.ResponseEntity;
/**
 * 
 * 
 * The import org.springframework cannot be resolved
 * 
 * **/

import org.springframework.web.bind.annotation.*;
/**
 * 
 * The import org.springframework cannot be resolved
 * 
 * **/


@RestController
/**
 * RestController cannot be resolved to a type
 * 
 * **/




@RequestMapping("/api/auth")

/**
 * 
 * RequestMapping cannot be resolved to a type
 * 
 * 
 * 
 * **/

public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    
    /**
     * 
     * PostMapping cannot be resolved to a type
     * 
     * 
     * **/
    
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
    	/**
    	 * 
    	 * Multiple markers at this line
	- AuthResponse cannot be resolved to a type
	- ResponseEntity cannot be resolved to a type
	- RequestBody cannot be resolved to a type
	- AuthRequest cannot be resolved to a type
    	 * 
    	 * 
    	 * **/
    	
    	
        // Simula validação de usuário
        if ("admin".equals(request.username()) && "senha123".equals(request.password())) {
            String token = jwtTokenProvider.generateToken(request.username());
            return ResponseEntity.ok(new AuthResponse("Bearer " + token));
            /**
             * 
             * 
             * Multiple markers at this line
	- ResponseEntity cannot be resolved
	- AuthResponse cannot be resolved to a type
             * 
             * **/
            
            
            
        }
        return ResponseEntity.status(401).build();
        
        /**
         * ResponseEntity cannot be resolved
         * 
         * 
         * **/
    }
}

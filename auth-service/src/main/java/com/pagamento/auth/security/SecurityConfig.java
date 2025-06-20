package com.pagamento.auth.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
/**
 * The import org.springframework.security.config cannot be resolved
 * The import org.springframework.security.core cannot be resolved
 * 
 * The import org.springframework.security.web cannot be resolved
 * 
 * The import org.springframework.security.web cannot be resolved
 * 
 * **/
import com.pagamento.common.model.User;

import jakarta.servlet.FilterChain;
/**
 * 
 * The import jakarta cannot be resolved
 * 
 * 
 * 
 * **/


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
/**
 * 
 * Configuration cannot be resolved to a type
 * 
 * **/

public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    
    /*
     * 
     * Bean cannot be resolved to a type
     * 
     * ***/
    
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	
    	/*
    	 * Multiple markers at this line
	- HttpSecurity cannot be resolved to a type
	- SecurityFilterChain cannot be resolved to a type
    	 * 
    	 * ***/
    	
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            /**
             * 
             * UsernamePasswordAuthenticationFilter cannot be resolved to a type
             * 
             * ***/
            
            
            .build();
    }

    public static class JwtAuthenticationFilter extends OncePerRequestFilter {
        private final JwtTokenProvider jwtTokenProvider;

        public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
            this.jwtTokenProvider = jwtTokenProvider;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            String token = resolveToken(request);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null,
                            Collections.singleton(new User(username, "", Collections.emptyList())));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        }

        private String resolveToken(HttpServletRequest request) {
        	/**
        	 * 
        	 * 
        	 * HttpServletRequest cannot be resolved to a type
        	 * 
        	 * **/
        	
            String bearer = request.getHeader("Authorization");
            if (bearer != null && bearer.startsWith("Bearer ")) {
                return bearer.substring(7);
            }
            return null;
        }
    }
}

package com.pagamento.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${cors.allowed-origins:*}")
    private String[] allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String[] allowedMethods;

    @Value("${cors.allowed-headers:*}")
    private String[] allowedHeaders;

    @Value("${cors.exposed-headers:}")
    private String[] exposedHeaders;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(getAllowedOrigins()));
        config.setAllowedMethods(Arrays.asList(allowedMethods));
        config.setAllowedHeaders(Arrays.asList(allowedHeaders));
        config.setExposedHeaders(Arrays.asList(exposedHeaders));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);  // 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }

	public String[] getAllowedOrigins() {
		return allowedOrigins;
	}

	public void setAllowedOrigins(String[] allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}
}
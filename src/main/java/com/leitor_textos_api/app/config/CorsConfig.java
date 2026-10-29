package com.leitor_textos_api.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Define explicitamente a origem permitida (sem usar "*")
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Permite todos os cabeçalhos (incluindo o seu Usuario-Id)
        configuration.setAllowedHeaders(List.of("*"));

        // Permite o envio de credenciais/tokens de segurança
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta regra a todas as rotas da sua API
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
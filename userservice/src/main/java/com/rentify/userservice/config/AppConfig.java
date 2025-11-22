package com.rentify.userservice.config;

import com.rentify.userservice.dto.UsuarioDTO;
import com.rentify.userservice.model.Usuario;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuración de beans de la aplicación User Service
 * Incluye ModelMapper para DTOs, WebClient para comunicación entre microservicios
 * y OpenAPI para documentación Swagger
 */
@Configuration
public class AppConfig {

    /**
     * Bean de ModelMapper para convertir entre DTOs y Entidades
     * Configurado para ignorar campos opcionales en DTOs
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setAmbiguityIgnored(true);

        // Configurar mapeo de UsuarioDTO a Usuario
        // Ignorar los campos 'rol' y 'estado' porque usamos 'rolId' y 'estadoId'
        mapper.typeMap(UsuarioDTO.class, Usuario.class)
                .addMappings(mapping -> mapping.skip(Usuario::setId));

        return mapper;
    }

    /**
     * Bean de WebClient.Builder para comunicación con otros microservicios
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    /**
     * Configuración de OpenAPI/Swagger para documentación de la API
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rentify - User Service API")
                        .version("1.0")
                        .description("API para gestión de usuarios, roles y autenticación en Rentify. " +
                                "Maneja registro, login, actualización de datos y asignación de roles.")
                        .contact(new Contact()
                                .name("Rentify Team")
                                .email("support@rentify.com")));
    }
}
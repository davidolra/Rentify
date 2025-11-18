package com.rentify.documentService.client;

import com.rentify.documentService.dto.external.UsuarioDTO;
import com.rentify.documentService.exception.MicroserviceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Cliente WebClient para comunicación con User Service.
 * Proporciona métodos para consultar información de usuarios.
 */
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${microservices.user-service.url}")
    private String userServiceUrl;

    /**
     * Obtiene un usuario por su ID desde User Service.
     *
     * @param userId ID del usuario a consultar
     * @return UsuarioDTO con la información del usuario, o null si no existe
     */
    public UsuarioDTO getUserById(Long userId) {
        try {
            log.debug("Consultando usuario con ID: {}", userId);

            return webClientBuilder.build()
                    .get()
                    .uri(userServiceUrl + "/api/usuarios/" + userId)
                    .retrieve()
                    .bodyToMono(UsuarioDTO.class)
                    .timeout(Duration.ofSeconds(5))
                    .onErrorResume(error -> {
                        log.error("Error al obtener usuario {}: {}", userId, error.getMessage());
                        return Mono.empty();
                    })
                    .block();
        } catch (Exception e) {
            log.error("Error crítico al comunicarse con User Service: {}", e.getMessage());
            throw new MicroserviceException("No se pudo verificar el usuario. Intente nuevamente.");
        }
    }

    /**
     * Verifica si un usuario existe en el sistema.
     *
     * @param userId ID del usuario a verificar
     * @return true si el usuario existe, false en caso contrario
     */
    public boolean existsUser(Long userId) {
        try {
            log.debug("Verificando existencia del usuario con ID: {}", userId);
            UsuarioDTO user = getUserById(userId);
            return user != null && user.getId() != null;
        } catch (Exception e) {
            log.error("Error al verificar existencia del usuario {}: {}", userId, e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si un usuario tiene un rol específico.
     *
     * @param userId ID del usuario
     * @param rol Rol a verificar
     * @return true si el usuario tiene el rol especificado
     */
    public boolean userHasRole(Long userId, String rol) {
        try {
            log.debug("Verificando si usuario {} tiene rol: {}", userId, rol);
            UsuarioDTO user = getUserById(userId);
            return user != null && rol.equals(user.getRol());
        } catch (Exception e) {
            log.error("Error al verificar rol del usuario {}: {}", userId, e.getMessage());
            return false;
        }
    }
}
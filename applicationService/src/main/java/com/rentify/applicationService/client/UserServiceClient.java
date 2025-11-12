package com.rentify.applicationService.client;

import com.rentify.applicationService.dto.external.UserResponse;
import com.rentify.applicationService.exception.ResourceNotFoundException;
import com.rentify.applicationService.exception.ServiceCommunicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${microservices.user-service.url}")
    private String userServiceUrl;

    public UserResponse getUserById(Long userId) {
        try {
            log.debug("Llamando a User Service para obtener usuario con ID: {}", userId);

            return webClientBuilder.build()
                    .get()
                    .uri(userServiceUrl + "/api/usuarios/{id}", userId)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .block();

        } catch (WebClientResponseException.NotFound e) {
            log.error("Usuario no encontrado con ID: {}", userId);
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + userId);
        } catch (Exception e) {
            log.error("Error al comunicarse con User Service: {}", e.getMessage());
            throw new ServiceCommunicationException("Error al comunicarse con User Service", e);
        }
    }

    public boolean validateUserExists(Long userId) {
        try {
            getUserById(userId);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
}
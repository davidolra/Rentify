package com.rentify.applicationService.client;

import com.rentify.applicationService.dto.PropiedadDTO;
import com.rentify.applicationService.exception.MicroserviceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class PropertyServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${microservices.property-service.url}")
    private String propertyServiceUrl;

    public PropiedadDTO getPropertyById(Long propertyId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(propertyServiceUrl + "/api/propiedades/" + propertyId)
                    .retrieve()
                    .bodyToMono(PropiedadDTO.class)
                    .timeout(Duration.ofSeconds(5))
                    .onErrorResume(error -> {
                        log.error("Error al obtener propiedad {}: {}", propertyId, error.getMessage());
                        return Mono.empty();
                    })
                    .block();
        } catch (Exception e) {
            log.error("Error crítico al comunicarse con Property Service: {}", e.getMessage());
            throw new MicroserviceException("No se pudo verificar la propiedad. Intente nuevamente.");
        }
    }

    public boolean existsProperty(Long propertyId) {
        try {
            PropiedadDTO property = getPropertyById(propertyId);
            return property != null && property.getId() != null;
        } catch (Exception e) {
            log.error("Error al verificar existencia de la propiedad {}: {}", propertyId, e.getMessage());
            return false;
        }
    }

    public boolean isPropertyAvailable(Long propertyId) {
        try {
            PropiedadDTO property = getPropertyById(propertyId);
            return property != null && Boolean.TRUE.equals(property.getDisponible());
        } catch (Exception e) {
            log.error("Error al verificar disponibilidad de la propiedad {}: {}", propertyId, e.getMessage());
            return false;
        }
    }
}
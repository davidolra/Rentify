package com.rentify.applicationService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información básica de la propiedad (desde Property Service)")
public class PropiedadDTO {

    @Schema(description = "ID de la propiedad", example = "1")
    private Long id;

    @Schema(description = "Título de la propiedad", example = "Dpto 2D/1B - Providencia")
    private String titulo;

    @Schema(description = "Dirección completa", example = "Av. Providencia 1234, Providencia")
    private String direccion;

    @Schema(description = "Precio mensual en CLP", example = "650000")
    private Double precio;

    @Schema(description = "Tipo de propiedad", example = "Departamento")
    private String tipo;

    @Schema(description = "Indica si la propiedad está disponible", example = "true")
    private Boolean disponible;
}
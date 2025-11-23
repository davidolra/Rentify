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

    @Schema(description = "ID de la propiedad", example = "7")
    private Long id;

    @Schema(description = "Código único de la propiedad", example = "DP007")
    private String codigo;

    @Schema(description = "Título de la propiedad", example = "Departamento 2D/2B Amoblado – Providencia")
    private String titulo;

    @Schema(description = "Dirección completa", example = "Av. Providencia 1234, Depto 501")
    private String direccion;

    @Schema(description = "Precio mensual en CLP", example = "650000.00")
    private Double precioMensual;

    @Schema(description = "Divisa del precio", example = "CLP")
    private String divisa;

    @Schema(description = "Metros cuadrados de la propiedad", example = "65.50")
    private Double m2;

    @Schema(description = "Número de habitaciones", example = "2")
    private Integer nHabit;

    @Schema(description = "Número de baños", example = "2")
    private Integer nBanos;

    @Schema(description = "Indica si la propiedad es pet-friendly", example = "true")
    private Boolean petFriendly;

    @Schema(description = "ID del tipo de propiedad", example = "1")
    private Integer tipoId;

    @Schema(description = "ID de la comuna", example = "1")
    private Integer comunaId;

    @Schema(description = "Fecha de creación", example = "2025-11-23")
    private String fcreacion;

    @Schema(description = "Información del tipo de propiedad")
    private TipoInfo tipo;

    @Schema(description = "Información de la comuna")
    private ComunaInfo comuna;

    /**
     * Clase interna para mapear la información del tipo
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TipoInfo {
        @Schema(description = "ID del tipo", example = "1")
        private Integer id;

        @Schema(description = "Nombre del tipo", example = "Departamento")
        private String nombre;
    }

    /**
     * Clase interna para mapear la información de la comuna
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComunaInfo {
        @Schema(description = "ID de la comuna", example = "1")
        private Integer id;

        @Schema(description = "Nombre de la comuna", example = "Providencia")
        private String nombre;
    }
}
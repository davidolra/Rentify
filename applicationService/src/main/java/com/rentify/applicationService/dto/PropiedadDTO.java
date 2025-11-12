package com.rentify.applicationService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// DTO para información de la propiedad (desde Property Service)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropiedadDTO {
    private Long id;
    private String titulo;
    private String direccion;
    private Double precio;
    private String tipo;
    private Boolean disponible;
}
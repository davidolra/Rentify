package com.rentify.applicationService.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para información del usuario proveniente del User Service
 * Contiene datos básicos del usuario necesarios para validaciones
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información básica del usuario (desde User Service)")
public class UsuarioDTO {

    @Schema(description = "ID único del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String nombre;

    @Schema(description = "Email del usuario", example = "juan.perez@email.com")
    private String email;

    @Schema(description = "Teléfono de contacto", example = "+56912345678")
    private String telefono;

    @Schema(description = "Rol del usuario en el sistema",
            example = "ARRIENDATARIO",
            allowableValues = {"ADMIN", "PROPIETARIO", "ARRIENDATARIO"})
    private String rol;

    @Schema(description = "Estado actual del usuario",
            example = "ACTIVO",
            allowableValues = {"ACTIVO", "INACTIVO"})
    private String estado;

    @Schema(description = "Indica si el usuario es VIP (correo @duoc.cl)", example = "false")
    private Boolean duocVip;
}
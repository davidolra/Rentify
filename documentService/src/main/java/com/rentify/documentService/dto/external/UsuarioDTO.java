package com.rentify.documentService.dto.external;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO externo para información de usuarios desde User Service.
 * Solo contiene campos necesarios para Document Service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Información del usuario desde User Service")
public class UsuarioDTO {

    @Schema(description = "ID único del usuario", example = "1")
    private Long id;

    @Schema(description = "Primer nombre del usuario", example = "Juan")
    private String pnombre;

    @Schema(description = "Primer apellido del usuario", example = "Pérez")
    private String papellido;

    @Schema(description = "Email del usuario", example = "juan.perez@email.com")
    private String email;

    @Schema(description = "Rol del usuario en el sistema",
            example = "ARRIENDATARIO",
            allowableValues = {"ADMIN", "PROPIETARIO", "ARRIENDATARIO"})
    private String rol;

    @Schema(description = "Estado del usuario", example = "Activo")
    private String estado;
}
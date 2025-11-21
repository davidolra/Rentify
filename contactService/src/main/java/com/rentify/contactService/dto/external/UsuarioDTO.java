package com.rentify.contactService.dto.external;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del usuario desde User Service")
public class UsuarioDTO {
    private Long id;
    private String pnombre;
    private String snombre;
    private String papellido;
    private String email;
    private String rol;
    private String estado;
}
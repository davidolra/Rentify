package com.rentify.applicationService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudArriendoDTO {

    private Long id;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El ID de la propiedad es obligatorio")
    private Long propiedadId;

    private String estado;
    private Date fechaSolicitud;

    // Información adicional de otros microservicios
    private String nombreUsuario;
    private String emailUsuario;
    private String direccionPropiedad;
    private Double precioPropiedad;
}
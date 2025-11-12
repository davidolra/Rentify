package com.rentify.applicationService.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroArriendoDTO {

    private Long id;

    @NotNull(message = "El ID de la solicitud es obligatorio")
    private Long solicitudId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private Date fechaInicio;

    private Date fechaFin;

    @NotNull(message = "El monto mensual es obligatorio")
    @Positive(message = "El monto debe ser mayor a 0")
    private Double montoMensual;

    private Boolean activo;

    // Información relacionada
    private SolicitudArriendoDTO solicitud;
}
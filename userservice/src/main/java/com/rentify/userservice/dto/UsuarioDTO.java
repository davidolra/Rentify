package com.rentify.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de un usuario del sistema Rentify")
public class UsuarioDTO {

    @Schema(description = "ID único del usuario",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El primer nombre es obligatorio")
    @Size(max = 60, message = "El primer nombre no puede exceder 60 caracteres")
    @Schema(description = "Primer nombre del usuario", example = "Juan")
    private String pnombre;

    @NotBlank(message = "El segundo nombre es obligatorio")
    @Size(max = 60, message = "El segundo nombre no puede exceder 60 caracteres")
    @Schema(description = "Segundo nombre del usuario", example = "Carlos")
    private String snombre;

    @NotBlank(message = "El apellido paterno es obligatorio")
    @Size(max = 60, message = "El apellido no puede exceder 60 caracteres")
    @Schema(description = "Apellido paterno", example = "Pérez")
    private String papellido;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @Schema(description = "Fecha de nacimiento", example = "1995-05-15")
    private LocalDate fnacimiento;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Size(max = 200)
    @Schema(description = "Correo electrónico", example = "juan.perez@email.com")
    private String email;

    @NotBlank(message = "El RUT es obligatorio")
    @Pattern(regexp = "^\\d{7,8}-[\\dkK]$", message = "Formato de RUT inválido (ej: 12345678-9)")
    @Schema(description = "RUT chileno", example = "12345678-9")
    private String rut;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 9, max = 12, message = "El teléfono debe tener entre 9 y 12 caracteres")
    @Schema(description = "Número de teléfono", example = "987654321")
    private String ntelefono;

    @Schema(description = "Indica si el usuario tiene beneficio DUOC (20% descuento)",
            example = "false",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean duocVip;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Schema(description = "Contraseña del usuario (mínimo 8 caracteres)", example = "password123")
    private String clave;

    @Schema(description = "Puntos RentifyPoints acumulados",
            example = "0",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Integer puntos;

    @Schema(description = "Código de referido único para el programa de referidos",
            example = "ABC123XYZ",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String codigoRef;

    @Schema(description = "Fecha de creación de la cuenta",
            example = "2025-01-15",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate fcreacion;

    @Schema(description = "Fecha de última actualización",
            example = "2025-01-15",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate factualizacion;

    @NotNull(message = "El estado es obligatorio")
    @Schema(description = "ID del estado del usuario (1=ACTIVO, 2=INACTIVO, 3=SUSPENDIDO)",
            example = "1")
    private Long estadoId;

    @Schema(description = "ID del rol asignado (1=ADMIN, 2=PROPIETARIO, 3=ARRIENDATARIO)",
            example = "3")
    private Long rolId;

    // Campos opcionales para incluir en respuestas con includeDetails=true
    @Schema(description = "Información del rol del usuario",
            accessMode = Schema.AccessMode.READ_ONLY)
    private RolDTO rol;

    @Schema(description = "Información del estado del usuario",
            accessMode = Schema.AccessMode.READ_ONLY)
    private EstadoDTO estado;
}
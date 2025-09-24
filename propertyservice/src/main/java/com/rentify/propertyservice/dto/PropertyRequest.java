package com.rentify.propertyservice.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PropertyRequest {
    private String codigo;
    private String titulo;
    private BigDecimal precioMensual;
    private String divisa;
    private BigDecimal m2;
    private Integer nHabit;
    private Integer nBanos;
    private Boolean petFriendly;
    private String direccion;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fcreacion;
    private Long tipoId;
    private Long comunaId;
}

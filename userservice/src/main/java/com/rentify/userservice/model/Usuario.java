package com.rentify.userservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String pnombre;

    @Column(nullable = false, length = 60)
    private String snombre;

    @Column(nullable = false, length = 60)
    private String papellido;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fnacimiento;

    @Column(nullable = false, length = 200, unique = true)
    private String email;

    @Column(nullable = false, length = 10, unique = true)
    private String rut;

    @Column(nullable = false, length = 12)
    private String ntelefono;

    @Column(nullable = false)
    private Boolean duoc_vip = false;

    @Column(nullable = false, length = 100)
    private String clave;

    @Column(nullable = false)
    private Integer puntos = 0;

    @Column(nullable = false, length = 20)
    private String codigo_ref;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fcreacion;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate factualizacion;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Rol rol;
}

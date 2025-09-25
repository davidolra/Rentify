package com.rentify.reviewService.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId; // referencia a usuario (otro microservicio)

    @Column(nullable = false)
    private Long propertyId; // referencia a propiedad (otro microservicio)

    @Column(nullable = false)
    private int puntaje; // ej: 1 a 5 estrellas

    @Column(length = 500)
    private String comentario;

    @ManyToOne
    @JoinColumn(name = "tipo_resena_id", nullable = false)
    private TipoResena tipoResena;
}

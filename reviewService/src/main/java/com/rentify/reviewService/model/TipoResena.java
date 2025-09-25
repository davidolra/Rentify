package com.rentify.reviewService.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_resena")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoResena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre; // Ej: "Reseña de Propiedad", "Reseña de Usuario"
}

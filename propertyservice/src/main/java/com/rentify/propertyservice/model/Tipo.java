package com.rentify.propertyservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;
}

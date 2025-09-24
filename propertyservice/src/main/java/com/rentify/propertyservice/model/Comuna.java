package com.rentify.propertyservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comuna")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comuna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;
}

package com.rentify.propertyservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "propiedad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 5, nullable = false)
    private String codigo;

    @Column(length = 100, nullable = false)
    private String titulo;

    @Column(name = "precio_mensual", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioMensual;

    @Column(length = 20, nullable = false)
    private String divisa;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal m2;

    @Column(name = "n_habit")
    private Integer nHabit;

    @Column(name = "n_banos")
    private Integer nBanos;

    @Column(name = "pet_friendly")
    private Boolean petFriendly;

    @Column(length = 200)
    private String direccion;

    @Column(name = "fcreacion")
    private LocalDate fcreacion;

    @ManyToOne
    @JoinColumn(name = "tipo_id")
    private Tipo tipo;

    @ManyToOne
    @JoinColumn(name = "comuna_id")
    private Comuna comuna;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Foto> fotos;
}

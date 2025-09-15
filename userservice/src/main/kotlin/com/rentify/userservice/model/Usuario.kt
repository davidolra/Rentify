package com.rentify.userservice.model

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "usuarios")
data class  Usuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 60)
    val pnombre: String,

    @Column(nullable = false, length = 60)
    val snombre: String,

    @Column(nullable = false, length = 60)
    val papellido: String,

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    val fnacimiento: Date,

    @Column(nullable = false, length = 200, unique = true)
    val email: String,

    @Column(nullable = false, length = 10, unique = true)
    val rut: String,

    @Column(nullable = false, length = 12)
    val ntelefono: String,

    @Column(nullable = false)
    val duoc_vip: Boolean = false,

    @Column(nullable = false, length = 100)
    val clave: String,

    @Column(nullable = false)
    val puntos: Int = 0,

    @Column(nullable = false, length = 20)
    val codigo_ref: String,

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    val fcreacion: Date = Date(),

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    val factualizacion: Date = Date(),

    @ManyToOne
    @JoinColumn(name = "rol_id")
    val rol: Rol? = null
)

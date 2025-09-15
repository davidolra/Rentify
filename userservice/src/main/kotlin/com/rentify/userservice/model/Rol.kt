package com.rentify.userservice.model

import jakarta.persistence.*

@Entity
@Table(name = "rol")
data class Rol(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 60)
    val nombre: String
)

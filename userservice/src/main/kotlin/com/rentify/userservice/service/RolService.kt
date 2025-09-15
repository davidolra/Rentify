package com.rentify.userservice.service

import com.rentify.userservice.model.Rol
import com.rentify.userservice.repository.RolRepository
import org.springframework.stereotype.Service

@Service
class RolService(
    private val rolRepository: RolRepository
) {
    fun crearRol(rol: Rol): Rol {
        return rolRepository.save(rol)
    }

    fun obtenerRoles(): List<Rol> {
        return rolRepository.findAll()
    }

    fun obtenerRolPorId(id: Long): Rol? {
        return rolRepository.findById(id).orElse(null)
    }
}

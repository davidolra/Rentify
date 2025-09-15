package com.rentify.userservice.controller

import com.rentify.userservice.model.Rol
import com.rentify.userservice.service.RolService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/roles")
class RolController(
    private val rolService: RolService
) {

    @PostMapping("/crear")
    fun crearRol(@RequestBody rol: Rol): Rol {
        return rolService.crearRol(rol)
    }

    @GetMapping
    fun obtenerRoles(): List<Rol> {
        return rolService.obtenerRoles()
    }

    @GetMapping("/{id}")
    fun obtenerRolPorId(@PathVariable id: Long): Rol? {
        return rolService.obtenerRolPorId(id)
    }
}

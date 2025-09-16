package com.rentify.userservice.controller

import com.rentify.userservice.model.Usuario
import com.rentify.userservice.service.UsuarioService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/usuarios")
class UsuarioController(
    private val usuarioService: UsuarioService
) {

    @PostMapping("/registrar")
    fun registrarUsuario(@RequestBody usuario: Usuario): Usuario {
        return usuarioService.registrarUsuario(usuario)
    }

    @GetMapping
    fun obtenerUsuarios(): List<Usuario> {
        return usuarioService.obtenerUsuarios()
    }

    // Buscar usuario por id
    @GetMapping("/id/{id}")
    fun obtenerUsuarioPorId(@PathVariable id: Long): Usuario? {
        return usuarioService.buscarPorId(id)
    }
}

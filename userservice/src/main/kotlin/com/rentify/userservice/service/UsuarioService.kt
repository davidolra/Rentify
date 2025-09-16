package com.rentify.userservice.service

import com.rentify.userservice.model.Usuario
import com.rentify.userservice.repository.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class UsuarioService(
    private val usuarioRepository: UsuarioRepository
) {

    fun registrarUsuario(usuario: Usuario): Usuario {
        return usuarioRepository.save(usuario)
    }

    fun obtenerUsuarios(): List<Usuario> {
        return usuarioRepository.findAll()
    }

    fun buscarPorId(id: Long): Usuario? {
        return usuarioRepository.findById(id).orElse(null)
    }
}

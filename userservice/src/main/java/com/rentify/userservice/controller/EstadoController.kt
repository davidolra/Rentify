package com.rentify.userservice.controller

import com.rentify.userservice.dto.EstadoDTO
import com.rentify.userservice.service.EstadoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller para gestión de estados
 * Endpoints: POST /api/estados, GET /api/estados, GET /api/estados/{id}
 */
@RestController
@RequestMapping("/api/estados")
@RequiredArgsConstructor
@Tag(name = "Estados", description = "Gestión de estados de usuario (ACTIVO, INACTIVO, SUSPENDIDO)")
class EstadoController {
    private val estadoService: EstadoService? = null

    /**
     * Crea un nuevo estado
     * POST /api/estados
     */
    @PostMapping
    @Operation(summary = "Crear nuevo estado", description = "Crea un nuevo estado en el sistema")
    fun crearEstado(@RequestBody estadoDTO: @Valid EstadoDTO): ResponseEntity<EstadoDTO?> {
        val creado = estadoService!!.crearEstado(estadoDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body<EstadoDTO?>(creado)
    }

    /**
     * Obtiene todos los estados
     * GET /api/estados
     */
    @GetMapping
    @Operation(summary = "Listar todos los estados", description = "Obtiene la lista completa de estados del sistema")
    fun obtenerTodos(): ResponseEntity<MutableList<EstadoDTO?>?> {
        return ResponseEntity.ok<MutableList<EstadoDTO?>?>(estadoService!!.obtenerTodos())
    }

    /**
     * Obtiene un estado por su ID
     * GET /api/estados/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener estado por ID", description = "Obtiene un estado específico por su ID")
    fun obtenerPorId(
        @Parameter(description = "ID del estado", example = "1") @PathVariable id: Long?
    ): ResponseEntity<EstadoDTO?> {
        return ResponseEntity.ok<EstadoDTO?>(estadoService!!.obtenerPorId(id))
    }

    /**
     * Obtiene un estado por su nombre
     * GET /api/estados/nombre/{nombre}
     */
    @GetMapping("/nombre/{nombre}")
    @Operation(
        summary = "Obtener estado por nombre",
        description = "Obtiene un estado específico por su nombre (ACTIVO, INACTIVO, SUSPENDIDO)"
    )
    fun obtenerPorNombre(
        @Parameter(description = "Nombre del estado", example = "ACTIVO") @PathVariable nombre: String?
    ): ResponseEntity<EstadoDTO?> {
        return ResponseEntity.ok<EstadoDTO?>(estadoService!!.obtenerPorNombre(nombre))
    }
}
package com.rentify.documentService.controller;

import com.rentify.documentService.dto.DocumentoDTO;
import com.rentify.documentService.service.DocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de documentos de usuarios.
 * Provee endpoints para subir, consultar, actualizar y eliminar documentos.
 */
@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
@Tag(name = "Documentos", description = "Gestión de documentos de usuarios")
public class DocumentoController {

    private final DocumentoService documentoService;

    /**
     * Crea/sube un nuevo documento.
     */
    @PostMapping
    @Operation(summary = "Subir nuevo documento",
            description = "Crea un nuevo documento para un usuario. Requiere usuario válido con permisos adecuados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Documento creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o validación de negocio fallida"),
            @ApiResponse(responseCode = "503", description = "Servicio de usuarios no disponible")
    })
    public ResponseEntity<DocumentoDTO> crearDocumento(
            @Valid @RequestBody DocumentoDTO documentoDTO) {
        DocumentoDTO created = documentoService.crearDocumento(documentoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Lista todos los documentos del sistema.
     */
    @GetMapping
    @Operation(summary = "Listar todos los documentos",
            description = "Obtiene listado completo de documentos del sistema")
    public ResponseEntity<List<DocumentoDTO>> listarTodos(
            @Parameter(description = "Incluir detalles expandidos (usuario, estado, tipo)")
            @RequestParam(defaultValue = "false") boolean includeDetails) {
        return ResponseEntity.ok(documentoService.listarTodos(includeDetails));
    }

    /**
     * Obtiene un documento específico por ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener documento por ID",
            description = "Consulta un documento específico por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento encontrado"),
            @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    })
    public ResponseEntity<DocumentoDTO> obtenerPorId(
            @PathVariable Long id,
            @Parameter(description = "Incluir detalles expandidos")
            @RequestParam(defaultValue = "true") boolean includeDetails) {
        return ResponseEntity.ok(documentoService.obtenerPorId(id, includeDetails));
    }

    /**
     * Obtiene todos los documentos de un usuario específico.
     */
    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener documentos por usuario",
            description = "Lista todos los documentos de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documentos encontrados"),
            @ApiResponse(responseCode = "404", description = "Usuario no existe")
    })
    public ResponseEntity<List<DocumentoDTO>> obtenerPorUsuario(
            @PathVariable Long usuarioId,
            @Parameter(description = "Incluir detalles expandidos")
            @RequestParam(defaultValue = "true") boolean includeDetails) {
        return ResponseEntity.ok(documentoService.obtenerPorUsuario(usuarioId, includeDetails));
    }

    /**
     * Actualiza el estado de un documento.
     */
    @PatchMapping("/{id}/estado/{estadoId}")
    @Operation(summary = "Actualizar estado de documento",
            description = "Cambia el estado de un documento (ej: de PENDIENTE a ACEPTADO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Documento o estado no encontrado")
    })
    public ResponseEntity<DocumentoDTO> actualizarEstado(
            @PathVariable Long id,
            @PathVariable Long estadoId) {
        return ResponseEntity.ok(documentoService.actualizarEstado(id, estadoId));
    }

    /**
     * Verifica si un usuario tiene documentos aprobados.
     * Endpoint útil para otros microservicios.
     */
    @GetMapping("/usuario/{usuarioId}/aprobados")
    @Operation(summary = "Verificar documentos aprobados",
            description = "Verifica si un usuario tiene al menos un documento aprobado")
    public ResponseEntity<Boolean> hasApprovedDocuments(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(documentoService.hasApprovedDocuments(usuarioId));
    }

    /**
     * Elimina un documento.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar documento",
            description = "Elimina permanentemente un documento del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Documento eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    })
    public ResponseEntity<Void> eliminarDocumento(@PathVariable Long id) {
        documentoService.eliminarDocumento(id);
        return ResponseEntity.noContent().build();
    }
}
package com.rentify.applicationService.controller;

import com.rentify.applicationService.dto.SolicitudArriendoDTO;
import com.rentify.applicationService.service.SolicitudArriendoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
@Tag(name = "Solicitudes de Arriendo", description = "API para gestionar solicitudes de arriendo")
public class SolicitudController {

    private final SolicitudArriendoService service;

    @PostMapping
    @Operation(summary = "Crear una nueva solicitud de arriendo")
    public ResponseEntity<SolicitudArriendoDTO> crearSolicitud(@Valid @RequestBody SolicitudArriendoDTO dto) {
        SolicitudArriendoDTO created = service.crearSolicitud(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar todas las solicitudes")
    public ResponseEntity<List<SolicitudArriendoDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener solicitud por ID")
    public ResponseEntity<SolicitudArriendoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar solicitudes por usuario")
    public ResponseEntity<List<SolicitudArriendoDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));
    }

    @GetMapping("/propiedad/{propiedadId}")
    @Operation(summary = "Listar solicitudes por propiedad")
    public ResponseEntity<List<SolicitudArriendoDTO>> listarPorPropiedad(@PathVariable Long propiedadId) {
        return ResponseEntity.ok(service.listarPorPropiedad(propiedadId));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de una solicitud")
    public ResponseEntity<SolicitudArriendoDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return ResponseEntity.ok(service.actualizarEstado(id, estado));
    }
}
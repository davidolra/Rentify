package com.rentify.applicationService.controller;

import com.rentify.applicationService.model.SolicitudArriendo;
import com.rentify.applicationService.repository.SolicitudArriendoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudArriendoRepository repository;

    @PostMapping
    public ResponseEntity<SolicitudArriendo> crearSolicitud(@RequestBody SolicitudArriendo solicitud) {
        solicitud.setEstado("PENDIENTE");
        solicitud.setFechaSolicitud(new Date());
        return ResponseEntity.ok(repository.save(solicitud));
    }

    @GetMapping
    public List<SolicitudArriendo> listarTodas() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudArriendo> obtenerPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

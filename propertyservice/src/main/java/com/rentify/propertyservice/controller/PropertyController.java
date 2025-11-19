package com.rentify.propertyservice.controller;

import com.rentify.propertyservice.model.Property;
import com.rentify.propertyservice.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/propiedades")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    public ResponseEntity<Property> crear(@RequestBody PropertyRequest req) {
        Property creado = propertyService.crearProperty(req);
        return ResponseEntity.created(URI.create("/api/propiedades/" + creado.getId())).body(creado);
    }

    @GetMapping
    public List<Property> listar() {
        return propertyService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> obtener(@PathVariable Long id) {
        return propertyService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Property> actualizar(@PathVariable Long id, @RequestBody PropertyRequest req) {
        try {
            Property actualizado = propertyService.actualizar(id, req);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        propertyService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

package com.rentify.userservice.controller;

import com.rentify.userservice.model.Rol;
import com.rentify.userservice.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    @PostMapping("/crear")
    public ResponseEntity<Rol> crearRol(@RequestBody Rol rol) {
        Rol creado = rolService.crearRol(rol);
        return ResponseEntity.created(URI.create("/api/roles/" + creado.getId())).body(creado);
    }

    @GetMapping
    public List<Rol> obtenerRoles() {
        return rolService.obtenerRoles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rol> obtenerRolPorId(@PathVariable Long id) {
        return rolService.obtenerRolPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

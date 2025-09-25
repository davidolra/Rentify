package com.rentify.applicationService.controller;

import com.rentify.applicationService.model.RegistroArriendo;
import com.rentify.applicationService.repository.RegistroArriendoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registros")
@RequiredArgsConstructor
public class RegistroController {

    private final RegistroArriendoRepository repository;

    @PostMapping
    public ResponseEntity<RegistroArriendo> crearRegistro(@RequestBody RegistroArriendo registro) {
        return ResponseEntity.ok(repository.save(registro));
    }

    @GetMapping
    public List<RegistroArriendo> listarTodos() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroArriendo> obtenerPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

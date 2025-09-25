package com.rentify.reviewService.controller;

import com.rentify.reviewService.model.TipoResena;
import com.rentify.reviewService.repository.TipoResenaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipo-resenas")
@RequiredArgsConstructor
public class TipoResenaController {

    private final TipoResenaRepository tipoResenaRepository;

    @GetMapping
    public ResponseEntity<List<TipoResena>> getAll() {
        return ResponseEntity.ok(tipoResenaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoResena> getById(@PathVariable Long id) {
        return tipoResenaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TipoResena> create(@RequestBody TipoResena tipoResena) {
        return ResponseEntity.ok(tipoResenaRepository.save(tipoResena));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoResena> update(@PathVariable Long id, @RequestBody TipoResena tipoResena) {
        return tipoResenaRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(tipoResena.getNombre());
                    return ResponseEntity.ok(tipoResenaRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (tipoResenaRepository.existsById(id)) {
            tipoResenaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

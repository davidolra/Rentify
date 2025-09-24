package com.rentify.propertyservice.controller;

import com.rentify.propertyservice.model.Comuna;
import com.rentify.propertyservice.repository.ComunaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comunas")
public class ComunaController {

    private final ComunaRepository comunaRepository;

    public ComunaController(ComunaRepository comunaRepository) {
        this.comunaRepository = comunaRepository;
    }

    @PostMapping
    public Comuna crear(@RequestBody Comuna comuna) {
        return comunaRepository.save(comuna);
    }

    @GetMapping
    public List<Comuna> listar() {
        return comunaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Comuna buscarPorId(@PathVariable Long id) {
        return comunaRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Comuna actualizar(@PathVariable Long id, @RequestBody Comuna comuna) {
        return comunaRepository.findById(id).map(c -> {
            c.setNombre(comuna.getNombre());
            return comunaRepository.save(c);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        comunaRepository.deleteById(id);
    }
}

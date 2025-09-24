package com.rentify.propertyservice.controller;

import com.rentify.propertyservice.model.Tipo;
import com.rentify.propertyservice.repository.TipoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos")
public class TipoController {

    private final TipoRepository tipoRepository;

    public TipoController(TipoRepository tipoRepository) {
        this.tipoRepository = tipoRepository;
    }

    @PostMapping
    public Tipo crear(@RequestBody Tipo tipo) {
        return tipoRepository.save(tipo);
    }

    @GetMapping
    public List<Tipo> listar() {
        return tipoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Tipo buscarPorId(@PathVariable Long id) {
        return tipoRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Tipo actualizar(@PathVariable Long id, @RequestBody Tipo tipo) {
        return tipoRepository.findById(id).map(t -> {
            t.setNombre(tipo.getNombre());
            return tipoRepository.save(t);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        tipoRepository.deleteById(id);
    }
}

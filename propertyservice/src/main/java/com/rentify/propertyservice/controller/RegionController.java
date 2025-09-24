package com.rentify.propertyservice.controller;

import com.rentify.propertyservice.model.Region;
import com.rentify.propertyservice.repository.RegionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regiones")
public class RegionController {

    private final RegionRepository regionRepository;

    public RegionController(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @PostMapping
    public Region crear(@RequestBody Region region) {
        return regionRepository.save(region);
    }

    @GetMapping
    public List<Region> listar() {
        return regionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Region buscarPorId(@PathVariable Long id) {
        return regionRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Region actualizar(@PathVariable Long id, @RequestBody Region region) {
        return regionRepository.findById(id).map(r -> {
            r.setNombre(region.getNombre());
            return regionRepository.save(r);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        regionRepository.deleteById(id);
    }
}

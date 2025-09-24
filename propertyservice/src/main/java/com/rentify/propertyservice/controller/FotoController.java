package com.rentify.propertyservice.controller;

import com.rentify.propertyservice.model.Foto;
import com.rentify.propertyservice.service.FotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FotoController {

    private final FotoService fotoService;

    @PostMapping("/propiedades/{id}/fotos")
    public ResponseEntity<?> uploadFoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Foto f = fotoService.guardarFoto(id, file);
            return ResponseEntity.ok(f);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al guardar la foto");
        }
    }

    @GetMapping("/propiedades/{id}/fotos")
    public List<Foto> lisFotos(@PathVariable Long id) {
        return fotoService.listarFotos(id);
    }

    @DeleteMapping("/fotos/{fotoId}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long fotoId) {
        fotoService.eliminarFoto(fotoId);
        return ResponseEntity.noContent().build();
    }
}

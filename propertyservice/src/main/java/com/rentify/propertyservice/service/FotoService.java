package com.rentify.propertyservice.service;

import com.rentify.propertyservice.model.Foto;
import com.rentify.propertyservice.model.Property;
import com.rentify.propertyservice.repository.FotoRepository;
import com.rentify.propertyservice.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FotoService {

    private final FotoRepository fotoRepository;
    private final PropertyRepository propertyRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public Foto guardarFoto(Long propertyId, MultipartFile file) throws Exception {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Propiedad no encontrada"));

        // crea carpeta property-specific
        Path propertyFolder = Paths.get(uploadDir, "properties", String.valueOf(propertyId));
        Files.createDirectories(propertyFolder);

        String original = file.getOriginalFilename();
        String filename = System.currentTimeMillis() + "_" + (original != null ? original.replaceAll("\\s+","_") : "image");

        Path target = propertyFolder.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        String url = target.toString(); // para prototipo local guardamos la ruta. En prod sería URL pública

        Foto foto = Foto.builder()
                .nombre(original != null ? original : filename)
                .url(url)
                .property(property)
                .build();

        return fotoRepository.save(foto);
    }

    public List<Foto> listarFotos(Long propertyId) {
        return fotoRepository.findByPropertyId(propertyId);
    }

    public void eliminarFoto(Long fotoId) {
        fotoRepository.deleteById(fotoId);
    }
}

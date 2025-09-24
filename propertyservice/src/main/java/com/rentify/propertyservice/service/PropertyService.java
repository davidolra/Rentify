package com.rentify.propertyservice.service;

import com.rentify.propertyservice.dto.PropertyRequest;
import com.rentify.propertyservice.model.*;
import com.rentify.propertyservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final TipoRepository tipoRepository;
    private final ComunaRepository comunaRepository;

    public Property crearProperty(PropertyRequest req) {
        Tipo tipo = tipoRepository.findById(req.getTipoId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo no encontrado"));
        Comuna comuna = comunaRepository.findById(req.getComunaId())
                .orElseThrow(() -> new IllegalArgumentException("Comuna no encontrada"));

        Property p = Property.builder()
                .codigo(req.getCodigo())
                .titulo(req.getTitulo())
                .precioMensual(req.getPrecioMensual())
                .divisa(req.getDivisa())
                .m2(req.getM2())
                .nHabit(req.getNHabit())
                .nBanos(req.getNBanos())
                .petFriendly(req.getPetFriendly() != null ? req.getPetFriendly() : false)
                .direccion(req.getDireccion())
                .fcreacion(req.getFcreacion() != null ? req.getFcreacion() : LocalDate.now())
                .tipo(tipo)
                .comuna(comuna)
                .build();

        return propertyRepository.save(p);
    }

    public List<Property> listarTodas() {
        return propertyRepository.findAll();
    }

    public Optional<Property> obtenerPorId(Long id) {
        return propertyRepository.findById(id);
    }

    public Property actualizar(Long id, PropertyRequest req) {
        Property p = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Propiedad no encontrada"));

        if (req.getTitulo() != null) p.setTitulo(req.getTitulo());
        if (req.getPrecioMensual() != null) p.setPrecioMensual(req.getPrecioMensual());
        // ... setear otras propiedades similares
        return propertyRepository.save(p);
    }

    public void eliminar(Long id) {
        propertyRepository.deleteById(id);
    }
}

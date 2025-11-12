package com.rentify.applicationService.service;

import com.rentify.applicationService.client.PropertyServiceClient;
import com.rentify.applicationService.client.UserServiceClient;
import com.rentify.applicationService.dto.SolicitudArriendoDTO;
import com.rentify.applicationService.dto.external.PropertyResponse;
import com.rentify.applicationService.dto.external.UserResponse;
import com.rentify.applicationService.exception.ResourceNotFoundException;
import com.rentify.applicationService.model.SolicitudArriendo;
import com.rentify.applicationService.repository.SolicitudArriendoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitudArriendoService {

    private final SolicitudArriendoRepository repository;
    private final UserServiceClient userServiceClient;
    private final PropertyServiceClient propertyServiceClient;

    @Transactional
    public SolicitudArriendoDTO crearSolicitud(SolicitudArriendoDTO dto) {
        log.info("Creando solicitud de arriendo para usuario {} y propiedad {}",
                dto.getUsuarioId(), dto.getPropiedadId());

        // Validar que el usuario existe
        userServiceClient.validateUserExists(dto.getUsuarioId());

        // Validar que la propiedad existe y está disponible
        propertyServiceClient.validatePropertyAvailable(dto.getPropiedadId());

        SolicitudArriendo solicitud = SolicitudArriendo.builder()
                .usuarioId(dto.getUsuarioId())
                .propiedadId(dto.getPropiedadId())
                .estado("PENDIENTE")
                .fechaSolicitud(new Date())
                .build();

        SolicitudArriendo saved = repository.save(solicitud);
        log.info("Solicitud creada exitosamente con ID: {}", saved.getId());

        return convertToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<SolicitudArriendoDTO> listarTodas() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SolicitudArriendoDTO obtenerPorId(Long id) {
        SolicitudArriendo solicitud = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con ID: " + id));
        return convertToDTO(solicitud);
    }

    @Transactional(readOnly = true)
    public List<SolicitudArriendoDTO> listarPorUsuario(Long usuarioId) {
        userServiceClient.validateUserExists(usuarioId);
        return repository.findByUsuarioId(usuarioId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SolicitudArriendoDTO> listarPorPropiedad(Long propiedadId) {
        propertyServiceClient.validatePropertyExists(propiedadId);
        return repository.findByPropiedadId(propiedadId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SolicitudArriendoDTO actualizarEstado(Long id, String nuevoEstado) {
        SolicitudArriendo solicitud = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con ID: " + id));

        solicitud.setEstado(nuevoEstado);
        SolicitudArriendo updated = repository.save(solicitud);

        log.info("Estado de solicitud {} actualizado a {}", id, nuevoEstado);
        return convertToDTO(updated);
    }

    private SolicitudArriendoDTO convertToDTO(SolicitudArriendo solicitud) {
        SolicitudArriendoDTO dto = SolicitudArriendoDTO.builder()
                .id(solicitud.getId())
                .usuarioId(solicitud.getUsuarioId())
                .propiedadId(solicitud.getPropiedadId())
                .estado(solicitud.getEstado())
                .fechaSolicitud(solicitud.getFechaSolicitud())
                .build();

        // Enriquecer con datos de otros microservicios
        try {
            UserResponse user = userServiceClient.getUserById(solicitud.getUsuarioId());
            dto.setNombreUsuario(user.getNombre());
            dto.setEmailUsuario(user.getEmail());
        } catch (Exception e) {
            log.warn("No se pudo obtener información del usuario: {}", e.getMessage());
        }

        try {
            PropertyResponse property = propertyServiceClient.getPropertyById(solicitud.getPropiedadId());
            dto.setDireccionPropiedad(property.getDireccion());
            dto.setPrecioPropiedad(property.getPrecioArriendo());
        } catch (Exception e) {
            log.warn("No se pudo obtener información de la propiedad: {}", e.getMessage());
        }

        return dto;
    }
}
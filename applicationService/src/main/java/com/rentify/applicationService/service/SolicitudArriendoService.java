package com.rentify.applicationService.service;

import com.rentify.applicationService.client.PropertyServiceClient;
import com.rentify.applicationService.client.UserServiceClient;
import com.rentify.applicationService.dto.PropiedadDTO;
import com.rentify.applicationService.dto.SolicitudArriendoDTO;
import com.rentify.applicationService.dto.UsuarioDTO;
import com.rentify.applicationService.exception.BusinessValidationException;
import com.rentify.applicationService.exception.ResourceNotFoundException;
import com.rentify.applicationService.model.SolicitudArriendo;
import com.rentify.applicationService.repository.SolicitudArriendoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    @Transactional
    public SolicitudArriendoDTO crearSolicitud(SolicitudArriendoDTO solicitudDTO) {
        log.info("Creando nueva solicitud para usuario {} y propiedad {}",
                solicitudDTO.getUsuarioId(), solicitudDTO.getPropiedadId());

        // Validar que el usuario existe
        if (!userServiceClient.existsUser(solicitudDTO.getUsuarioId())) {
            throw new BusinessValidationException("El usuario con ID " + solicitudDTO.getUsuarioId() + " no existe");
        }

        // Validar que la propiedad existe y está disponible
        if (!propertyServiceClient.existsProperty(solicitudDTO.getPropiedadId())) {
            throw new BusinessValidationException("La propiedad con ID " + solicitudDTO.getPropiedadId() + " no existe");
        }

        if (!propertyServiceClient.isPropertyAvailable(solicitudDTO.getPropiedadId())) {
            throw new BusinessValidationException("La propiedad no está disponible para arriendo");
        }

        // Crear la solicitud
        SolicitudArriendo solicitud = modelMapper.map(solicitudDTO, SolicitudArriendo.class);
        solicitud.setEstado("PENDIENTE");
        solicitud.setFechaSolicitud(new Date());

        SolicitudArriendo saved = repository.save(solicitud);
        log.info("Solicitud creada con ID: {}", saved.getId());

        return convertToDTO(saved, true);
    }

    @Transactional(readOnly = true)
    public List<SolicitudArriendoDTO> listarTodas(boolean includeDetails) {
        return repository.findAll().stream()
                .map(s -> convertToDTO(s, includeDetails))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SolicitudArriendoDTO obtenerPorId(Long id, boolean includeDetails) {
        SolicitudArriendo solicitud = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con ID: " + id));
        return convertToDTO(solicitud, includeDetails);
    }

    @Transactional(readOnly = true)
    public List<SolicitudArriendoDTO> obtenerPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream()
                .map(s -> convertToDTO(s, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SolicitudArriendoDTO> obtenerPorPropiedad(Long propiedadId) {
        return repository.findByPropiedadId(propiedadId).stream()
                .map(s -> convertToDTO(s, false))
                .collect(Collectors.toList());
    }

    @Transactional
    public SolicitudArriendoDTO actualizarEstado(Long id, String nuevoEstado) {
        SolicitudArriendo solicitud = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con ID: " + id));

        if (!isValidEstado(nuevoEstado)) {
            throw new BusinessValidationException("Estado inválido: " + nuevoEstado);
        }

        solicitud.setEstado(nuevoEstado.toUpperCase());
        SolicitudArriendo updated = repository.save(solicitud);
        log.info("Estado de solicitud {} actualizado a: {}", id, nuevoEstado);

        return convertToDTO(updated, true);
    }

    private boolean isValidEstado(String estado) {
        return estado != null &&
                (estado.equalsIgnoreCase("PENDIENTE") ||
                        estado.equalsIgnoreCase("ACEPTADA") ||
                        estado.equalsIgnoreCase("RECHAZADA"));
    }

    private SolicitudArriendoDTO convertToDTO(SolicitudArriendo solicitud, boolean includeDetails) {
        SolicitudArriendoDTO dto = modelMapper.map(solicitud, SolicitudArriendoDTO.class);

        if (includeDetails) {
            try {
                UsuarioDTO usuario = userServiceClient.getUserById(solicitud.getUsuarioId());
                dto.setUsuario(usuario);
            } catch (Exception e) {
                log.warn("No se pudo obtener información del usuario {}", solicitud.getUsuarioId());
            }

            try {
                PropiedadDTO propiedad = propertyServiceClient.getPropertyById(solicitud.getPropiedadId());
                dto.setPropiedad(propiedad);
            } catch (Exception e) {
                log.warn("No se pudo obtener información de la propiedad {}", solicitud.getPropiedadId());
            }
        }

        return dto;
    }
}
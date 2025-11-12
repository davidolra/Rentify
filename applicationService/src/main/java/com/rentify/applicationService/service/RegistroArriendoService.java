package com.rentify.applicationService.service;

import com.rentify.applicationService.dto.RegistroArriendoDTO;
import com.rentify.applicationService.dto.SolicitudArriendoDTO;
import com.rentify.applicationService.exception.BusinessValidationException;
import com.rentify.applicationService.exception.ResourceNotFoundException;
import com.rentify.applicationService.model.RegistroArriendo;
import com.rentify.applicationService.model.SolicitudArriendo;
import com.rentify.applicationService.repository.RegistroArriendoRepository;
import com.rentify.applicationService.repository.SolicitudArriendoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistroArriendoService {

    private final RegistroArriendoRepository repository;
    private final SolicitudArriendoRepository solicitudRepository;
    private final SolicitudArriendoService solicitudService;
    private final ModelMapper modelMapper;

    @Transactional
    public RegistroArriendoDTO crearRegistro(RegistroArriendoDTO registroDTO) {
        log.info("Creando nuevo registro para solicitud {}", registroDTO.getSolicitudId());

        // Validar que la solicitud existe
        SolicitudArriendo solicitud = solicitudRepository.findById(registroDTO.getSolicitudId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Solicitud no encontrada con ID: " + registroDTO.getSolicitudId()));

        // Validar que la solicitud está ACEPTADA
        if (!"ACEPTADA".equals(solicitud.getEstado())) {
            throw new BusinessValidationException(
                    "Solo se pueden crear registros para solicitudes aceptadas. Estado actual: " + solicitud.getEstado());
        }

        // Validar que no existe ya un registro activo para esta solicitud
        List<RegistroArriendo> registrosActivos = repository.findBySolicitudId(registroDTO.getSolicitudId())
                .stream()
                .filter(RegistroArriendo::getActivo)
                .toList();

        if (!registrosActivos.isEmpty()) {
            throw new BusinessValidationException(
                    "Ya existe un registro activo para esta solicitud");
        }

        // Validar fechas
        if (registroDTO.getFechaFin() != null &&
                registroDTO.getFechaInicio().after(registroDTO.getFechaFin())) {
            throw new BusinessValidationException(
                    "La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        RegistroArriendo registro = modelMapper.map(registroDTO, RegistroArriendo.class);
        registro.setActivo(true);

        RegistroArriendo saved = repository.save(registro);
        log.info("Registro creado con ID: {}", saved.getId());

        return convertToDTO(saved, true);
    }

    @Transactional(readOnly = true)
    public List<RegistroArriendoDTO> listarTodos(boolean includeDetails) {
        return repository.findAll().stream()
                .map(r -> convertToDTO(r, includeDetails))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RegistroArriendoDTO obtenerPorId(Long id, boolean includeDetails) {
        RegistroArriendo registro = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado con ID: " + id));
        return convertToDTO(registro, includeDetails);
    }

    @Transactional(readOnly = true)
    public List<RegistroArriendoDTO> obtenerPorSolicitud(Long solicitudId) {
        return repository.findBySolicitudId(solicitudId).stream()
                .map(r -> convertToDTO(r, false))
                .collect(Collectors.toList());
    }

    @Transactional
    public RegistroArriendoDTO finalizarRegistro(Long id) {
        RegistroArriendo registro = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado con ID: " + id));

        if (!registro.getActivo()) {
            throw new BusinessValidationException("El registro ya está inactivo");
        }

        registro.setActivo(false);
        registro.setFechaFin(new java.util.Date());

        RegistroArriendo updated = repository.save(registro);
        log.info("Registro {} finalizado", id);

        return convertToDTO(updated, true);
    }

    private RegistroArriendoDTO convertToDTO(RegistroArriendo registro, boolean includeDetails) {
        RegistroArriendoDTO dto = modelMapper.map(registro, RegistroArriendoDTO.class);

        if (includeDetails) {
            try {
                SolicitudArriendoDTO solicitud = solicitudService.obtenerPorId(registro.getSolicitudId(), true);
                dto.setSolicitud(solicitud);
            } catch (Exception e) {
                log.warn("No se pudo obtener información de la solicitud {}", registro.getSolicitudId());
            }
        }

        return dto;
    }
}
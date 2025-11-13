package com.rentify.applicationService.service;

import com.rentify.applicationService.dto.RegistroArriendoDTO;
import com.rentify.applicationService.dto.SolicitudArriendoDTO;
import com.rentify.applicationService.exception.BusinessValidationException;
import com.rentify.applicationService.exception.ResourceNotFoundException;
import com.rentify.applicationService.model.RegistroArriendo;
import com.rentify.applicationService.model.SolicitudArriendo;
import com.rentify.applicationService.repository.RegistroArriendoRepository;
import com.rentify.applicationService.repository.SolicitudArriendoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para RegistroArriendoService")
class RegistroArriendoServiceTest {

    @Mock
    private RegistroArriendoRepository repository;

    @Mock
    private SolicitudArriendoRepository solicitudRepository;

    @Mock
    private SolicitudArriendoService solicitudService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RegistroArriendoService service;

    private RegistroArriendoDTO registroDTO;
    private RegistroArriendo registro;
    private SolicitudArriendo solicitud;
    private SolicitudArriendoDTO solicitudDTO;

    @BeforeEach
    void setUp() {
        Date fechaInicio = new Date();
        Date fechaFin = new Date(fechaInicio.getTime() + 86400000L); // +1 día

        registroDTO = RegistroArriendoDTO.builder()
                .solicitudId(1L)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .montoMensual(500000.0)
                .activo(true)
                .build();

        registro = RegistroArriendo.builder()
                .id(1L)
                .solicitudId(1L)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .montoMensual(500000.0)
                .activo(true)
                .build();

        solicitud = SolicitudArriendo.builder()
                .id(1L)
                .usuarioId(1L)
                .propiedadId(1L)
                .estado("ACEPTADA")
                .fechaSolicitud(new Date())
                .build();

        solicitudDTO = SolicitudArriendoDTO.builder()
                .id(1L)
                .usuarioId(1L)
                .propiedadId(1L)
                .estado("ACEPTADA")
                .build();
    }

    @Test
    @DisplayName("Crear registro exitosamente")
    void crearRegistro_DeberiaCrearRegistroExitosamente() {
        // Given
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(repository.findBySolicitudId(1L)).thenReturn(Arrays.asList());
        when(modelMapper.map(registroDTO, RegistroArriendo.class)).thenReturn(registro);
        when(repository.save(any(RegistroArriendo.class))).thenReturn(registro);
        when(modelMapper.map(registro, RegistroArriendoDTO.class)).thenReturn(registroDTO);
        when(solicitudService.obtenerPorId(1L, true)).thenReturn(solicitudDTO);

        // When
        RegistroArriendoDTO result = service.crearRegistro(registroDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSolicitudId()).isEqualTo(1L);
        assertThat(result.getMontoMensual()).isEqualTo(500000.0);

        verify(solicitudRepository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(RegistroArriendo.class));
    }

    @Test
    @DisplayName("Crear registro - Solicitud no existe")
    void crearRegistro_SolicitudNoExiste_DeberiaLanzarExcepcion() {
        // Given
        when(solicitudRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.crearRegistro(registroDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Solicitud no encontrada con ID: 1");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Crear registro - Solicitud no está aceptada")
    void crearRegistro_SolicitudNoAceptada_DeberiaLanzarExcepcion() {
        // Given
        solicitud.setEstado("PENDIENTE");
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

        // When & Then
        assertThatThrownBy(() -> service.crearRegistro(registroDTO))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("Solo se pueden crear registros para solicitudes aceptadas");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Crear registro - Ya existe registro activo")
    void crearRegistro_RegistroActivoExiste_DeberiaLanzarExcepcion() {
        // Given
        RegistroArriendo registroActivo = RegistroArriendo.builder()
                .id(2L)
                .solicitudId(1L)
                .activo(true)
                .build();

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(repository.findBySolicitudId(1L)).thenReturn(Arrays.asList(registroActivo));

        // When & Then
        assertThatThrownBy(() -> service.crearRegistro(registroDTO))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("Ya existe un registro activo");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Crear registro - Fecha fin antes de fecha inicio")
    void crearRegistro_FechaInvalida_DeberiaLanzarExcepcion() {
        // Given
        Date fechaInicio = new Date();
        Date fechaFin = new Date(fechaInicio.getTime() - 86400000L); // -1 día
        registroDTO.setFechaInicio(fechaInicio);
        registroDTO.setFechaFin(fechaFin);

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(repository.findBySolicitudId(1L)).thenReturn(Arrays.asList());

        // When & Then
        assertThatThrownBy(() -> service.crearRegistro(registroDTO))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("La fecha de inicio no puede ser posterior a la fecha de fin");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Listar todos los registros")
    void listarTodos_DeberiaRetornarListaDeRegistros() {
        // Given
        List<RegistroArriendo> registros = Arrays.asList(registro);
        when(repository.findAll()).thenReturn(registros);
        when(modelMapper.map(any(RegistroArriendo.class), eq(RegistroArriendoDTO.class)))
                .thenReturn(registroDTO);

        // When
        List<RegistroArriendoDTO> result = service.listarTodos(false);

        // Then
        assertThat(result).hasSize(1);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Obtener registro por ID - Exitoso")
    void obtenerPorId_RegistroExiste_DeberiaRetornarRegistro() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(registro));
        when(modelMapper.map(registro, RegistroArriendoDTO.class)).thenReturn(registroDTO);
        when(solicitudService.obtenerPorId(1L, true)).thenReturn(solicitudDTO);

        // When
        RegistroArriendoDTO result = service.obtenerPorId(1L, true);

        // Then
        assertThat(result).isNotNull();
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Obtener registro por ID - No existe")
    void obtenerPorId_RegistroNoExiste_DeberiaLanzarExcepcion() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.obtenerPorId(1L, false))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Registro no encontrado con ID: 1");
    }

    @Test
    @DisplayName("Obtener registros por solicitud")
    void obtenerPorSolicitud_DeberiaRetornarRegistrosDeLaSolicitud() {
        // Given
        List<RegistroArriendo> registros = Arrays.asList(registro);
        when(repository.findBySolicitudId(1L)).thenReturn(registros);
        when(modelMapper.map(any(RegistroArriendo.class), eq(RegistroArriendoDTO.class)))
                .thenReturn(registroDTO);

        // When
        List<RegistroArriendoDTO> result = service.obtenerPorSolicitud(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(repository, times(1)).findBySolicitudId(1L);
    }

    @Test
    @DisplayName("Finalizar registro - Exitoso")
    void finalizarRegistro_RegistroActivo_DeberiaFinalizarRegistro() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(registro));
        when(repository.save(any(RegistroArriendo.class))).thenReturn(registro);
        when(modelMapper.map(registro, RegistroArriendoDTO.class)).thenReturn(registroDTO);
        when(solicitudService.obtenerPorId(1L, true)).thenReturn(solicitudDTO);

        // When
        RegistroArriendoDTO result = service.finalizarRegistro(1L);

        // Then
        assertThat(result).isNotNull();
        verify(repository, times(1)).save(any(RegistroArriendo.class));
    }

    @Test
    @DisplayName("Finalizar registro - Ya está inactivo")
    void finalizarRegistro_RegistroInactivo_DeberiaLanzarExcepcion() {
        // Given
        registro.setActivo(false);
        when(repository.findById(1L)).thenReturn(Optional.of(registro));

        // When & Then
        assertThatThrownBy(() -> service.finalizarRegistro(1L))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("El registro ya está inactivo");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Finalizar registro - No existe")
    void finalizarRegistro_RegistroNoExiste_DeberiaLanzarExcepcion() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.finalizarRegistro(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).save(any());
    }
}
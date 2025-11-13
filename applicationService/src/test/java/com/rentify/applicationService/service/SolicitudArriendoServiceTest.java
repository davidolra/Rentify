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
@DisplayName("Tests para SolicitudArriendoService")
class SolicitudArriendoServiceTest {

    @Mock
    private SolicitudArriendoRepository repository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PropertyServiceClient propertyServiceClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SolicitudArriendoService service;

    private SolicitudArriendoDTO solicitudDTO;
    private SolicitudArriendo solicitud;
    private UsuarioDTO usuarioDTO;
    private PropiedadDTO propiedadDTO;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba
        solicitudDTO = SolicitudArriendoDTO.builder()
                .usuarioId(1L)
                .propiedadId(1L)
                .build();

        solicitud = SolicitudArriendo.builder()
                .id(1L)
                .usuarioId(1L)
                .propiedadId(1L)
                .estado("PENDIENTE")
                .fechaSolicitud(new Date())
                .build();

        usuarioDTO = new UsuarioDTO(1L, "Juan Pérez", "juan@example.com", "123456789");
        propiedadDTO = new PropiedadDTO(1L, "Casa en Santiago", "Av. Principal 123", 500000.0, "Casa", true);
    }

    @Test
    @DisplayName("Crear solicitud exitosamente")
    void crearSolicitud_DeberiaCrearSolicitudExitosamente() {
        // Given
        when(userServiceClient.existsUser(1L)).thenReturn(true);
        when(propertyServiceClient.existsProperty(1L)).thenReturn(true);
        when(propertyServiceClient.isPropertyAvailable(1L)).thenReturn(true);
        when(modelMapper.map(solicitudDTO, SolicitudArriendo.class)).thenReturn(solicitud);
        when(repository.save(any(SolicitudArriendo.class))).thenReturn(solicitud);
        when(modelMapper.map(solicitud, SolicitudArriendoDTO.class)).thenReturn(solicitudDTO);
        when(userServiceClient.getUserById(1L)).thenReturn(usuarioDTO);
        when(propertyServiceClient.getPropertyById(1L)).thenReturn(propiedadDTO);

        // When
        SolicitudArriendoDTO result = service.crearSolicitud(solicitudDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsuarioId()).isEqualTo(1L);
        assertThat(result.getPropiedadId()).isEqualTo(1L);

        verify(userServiceClient, times(1)).existsUser(1L);
        verify(propertyServiceClient, times(1)).existsProperty(1L);
        verify(propertyServiceClient, times(1)).isPropertyAvailable(1L);
        verify(repository, times(1)).save(any(SolicitudArriendo.class));
    }

    @Test
    @DisplayName("Crear solicitud - Usuario no existe")
    void crearSolicitud_UsuarioNoExiste_DeberiaLanzarExcepcion() {
        // Given
        when(userServiceClient.existsUser(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> service.crearSolicitud(solicitudDTO))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("El usuario con ID 1 no existe");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Crear solicitud - Propiedad no existe")
    void crearSolicitud_PropiedadNoExiste_DeberiaLanzarExcepcion() {
        // Given
        when(userServiceClient.existsUser(1L)).thenReturn(true);
        when(propertyServiceClient.existsProperty(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> service.crearSolicitud(solicitudDTO))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("La propiedad con ID 1 no existe");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Crear solicitud - Propiedad no disponible")
    void crearSolicitud_PropiedadNoDisponible_DeberiaLanzarExcepcion() {
        // Given
        when(userServiceClient.existsUser(1L)).thenReturn(true);
        when(propertyServiceClient.existsProperty(1L)).thenReturn(true);
        when(propertyServiceClient.isPropertyAvailable(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> service.crearSolicitud(solicitudDTO))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("La propiedad no está disponible");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Listar todas las solicitudes")
    void listarTodas_DeberiaRetornarListaDeSolicitudes() {
        // Given
        List<SolicitudArriendo> solicitudes = Arrays.asList(solicitud);
        when(repository.findAll()).thenReturn(solicitudes);
        when(modelMapper.map(any(SolicitudArriendo.class), eq(SolicitudArriendoDTO.class)))
                .thenReturn(solicitudDTO);

        // When
        List<SolicitudArriendoDTO> result = service.listarTodas(false);

        // Then
        assertThat(result).hasSize(1);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Obtener solicitud por ID - Exitoso")
    void obtenerPorId_SolicitudExiste_DeberiaRetornarSolicitud() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(modelMapper.map(solicitud, SolicitudArriendoDTO.class)).thenReturn(solicitudDTO);
        when(userServiceClient.getUserById(1L)).thenReturn(usuarioDTO);
        when(propertyServiceClient.getPropertyById(1L)).thenReturn(propiedadDTO);

        // When
        SolicitudArriendoDTO result = service.obtenerPorId(1L, true);

        // Then
        assertThat(result).isNotNull();
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Obtener solicitud por ID - No existe")
    void obtenerPorId_SolicitudNoExiste_DeberiaLanzarExcepcion() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.obtenerPorId(1L, false))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Solicitud no encontrada con ID: 1");
    }

    @Test
    @DisplayName("Obtener solicitudes por usuario")
    void obtenerPorUsuario_DeberiaRetornarSolicitudesDelUsuario() {
        // Given
        List<SolicitudArriendo> solicitudes = Arrays.asList(solicitud);
        when(repository.findByUsuarioId(1L)).thenReturn(solicitudes);
        when(modelMapper.map(any(SolicitudArriendo.class), eq(SolicitudArriendoDTO.class)))
                .thenReturn(solicitudDTO);

        // When
        List<SolicitudArriendoDTO> result = service.obtenerPorUsuario(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(repository, times(1)).findByUsuarioId(1L);
    }

    @Test
    @DisplayName("Obtener solicitudes por propiedad")
    void obtenerPorPropiedad_DeberiaRetornarSolicitudesDeLaPropiedad() {
        // Given
        List<SolicitudArriendo> solicitudes = Arrays.asList(solicitud);
        when(repository.findByPropiedadId(1L)).thenReturn(solicitudes);
        when(modelMapper.map(any(SolicitudArriendo.class), eq(SolicitudArriendoDTO.class)))
                .thenReturn(solicitudDTO);

        // When
        List<SolicitudArriendoDTO> result = service.obtenerPorPropiedad(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(repository, times(1)).findByPropiedadId(1L);
    }

    @Test
    @DisplayName("Actualizar estado - Exitoso")
    void actualizarEstado_EstadoValido_DeberiaActualizarEstado() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(repository.save(any(SolicitudArriendo.class))).thenReturn(solicitud);
        when(modelMapper.map(solicitud, SolicitudArriendoDTO.class)).thenReturn(solicitudDTO);
        when(userServiceClient.getUserById(1L)).thenReturn(usuarioDTO);
        when(propertyServiceClient.getPropertyById(1L)).thenReturn(propiedadDTO);

        // When
        SolicitudArriendoDTO result = service.actualizarEstado(1L, "ACEPTADA");

        // Then
        assertThat(result).isNotNull();
        verify(repository, times(1)).save(any(SolicitudArriendo.class));
    }

    @Test
    @DisplayName("Actualizar estado - Estado inválido")
    void actualizarEstado_EstadoInvalido_DeberiaLanzarExcepcion() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(solicitud));

        // When & Then
        assertThatThrownBy(() -> service.actualizarEstado(1L, "INVALIDO"))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("Estado inválido");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar estado - Solicitud no existe")
    void actualizarEstado_SolicitudNoExiste_DeberiaLanzarExcepcion() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.actualizarEstado(1L, "ACEPTADA"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).save(any());
    }
}
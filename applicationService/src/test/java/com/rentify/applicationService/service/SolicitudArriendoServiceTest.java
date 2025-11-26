//package com.rentify.applicationService.service;
//
//import com.rentify.applicationService.client.DocumentServiceClient;
//import com.rentify.applicationService.client.PropertyServiceClient;
//import com.rentify.applicationService.client.UserServiceClient;
//import com.rentify.applicationService.dto.PropiedadDTO;
//import com.rentify.applicationService.dto.SolicitudArriendoDTO;
//import com.rentify.applicationService.dto.UsuarioDTO;
//import com.rentify.applicationService.exception.BusinessValidationException;
//import com.rentify.applicationService.exception.ResourceNotFoundException;
//import com.rentify.applicationService.model.SolicitudArriendo;
//import com.rentify.applicationService.repository.SolicitudArriendoRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.modelmapper.ModelMapper;
//
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
///**
// * Tests unitarios para SolicitudArriendoService
// * Valida toda la lógica de negocio del servicio
// */
//@ExtendWith(MockitoExtension.class)
//@DisplayName("Tests de SolicitudArriendoService")
//class SolicitudArriendoServiceTest {
//
//    @Mock
//    private SolicitudArriendoRepository repository;
//
//    @Mock
//    private UserServiceClient userServiceClient;
//
//    @Mock
//    private PropertyServiceClient propertyServiceClient;
//
//    @Mock
//    private DocumentServiceClient documentServiceClient;
//
//    @Mock
//    private ModelMapper modelMapper;
//
//    @InjectMocks
//    private SolicitudArriendoService service;
//
//    private UsuarioDTO usuarioValido;
//    private PropiedadDTO propiedadValida;
//    private SolicitudArriendoDTO solicitudDTO;
//    private SolicitudArriendo solicitudEntity;
//
//    @BeforeEach
//    void setUp() {
//        // Usuario válido con rol ARRIENDATARIO
//        usuarioValido = new UsuarioDTO();
//        usuarioValido.setId(1L);
//        usuarioValido.setNombre("Juan Pérez");
//        usuarioValido.setEmail("juan@email.com");
//        usuarioValido.setRol("ARRIENDATARIO");
//        usuarioValido.setEstado("ACTIVO");
//
//        // Propiedad válida y disponible
//        propiedadValida = new PropiedadDTO();
//        propiedadValida.setId(1L);
//        propiedadValida.setTitulo("Depto 2D/1B");
//        propiedadValida.setPrecio(500000.0);
//        propiedadValida.setDisponible(true);
//
//        // DTO de solicitud
//        solicitudDTO = SolicitudArriendoDTO.builder()
//                .usuarioId(1L)
//                .propiedadId(1L)
//                .build();
//
//        // Entidad de solicitud
//        solicitudEntity = new SolicitudArriendo();
//        solicitudEntity.setId(1L);
//        solicitudEntity.setUsuarioId(1L);
//        solicitudEntity.setPropiedadId(1L);
//        solicitudEntity.setEstado("PENDIENTE");
//        solicitudEntity.setFechaSolicitud(new Date());
//    }
//
//    @Test
//    @DisplayName("Debe crear solicitud exitosamente cuando todos los datos son válidos")
//    void crearSolicitud_DatosValidos_Success() {
//        // Arrange
//        when(userServiceClient.getUserById(1L)).thenReturn(usuarioValido);
//        when(repository.countByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(0L);
//        when(repository.existsByUsuarioIdAndPropiedadIdAndEstado(1L, 1L, "PENDIENTE")).thenReturn(false);
//        when(propertyServiceClient.existsProperty(1L)).thenReturn(true);
//        when(propertyServiceClient.isPropertyAvailable(1L)).thenReturn(true);
//        when(documentServiceClient.hasApprovedDocuments(1L)).thenReturn(true);
//        when(modelMapper.map(any(SolicitudArriendoDTO.class), eq(SolicitudArriendo.class)))
//                .thenReturn(solicitudEntity);
//        when(repository.save(any(SolicitudArriendo.class))).thenReturn(solicitudEntity);
//        when(modelMapper.map(any(SolicitudArriendo.class), eq(SolicitudArriendoDTO.class)))
//                .thenReturn(solicitudDTO);
//
//        // Act
//        SolicitudArriendoDTO resultado = service.crearSolicitud(solicitudDTO);
//
//        // Assert
//        assertThat(resultado).isNotNull();
//        verify(repository, times(1)).save(any(SolicitudArriendo.class));
//        verify(userServiceClient, times(2)).getUserById(1L); // 1 para validación + 1 para detalles
//    }
//
//    @Test
//    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
//    void crearSolicitud_UsuarioNoExiste_ThrowsException() {
//        // Arrange
//        when(userServiceClient.getUserById(1L)).thenReturn(null);
//
//        // Act & Assert
//        assertThatThrownBy(() -> service.crearSolicitud(solicitudDTO))
//                .isInstanceOf(BusinessValidationException.class)
//                .hasMessageContaining("usuario con ID 1 no existe");
//
//        verify(repository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Debe lanzar excepción cuando el usuario no tiene rol ARRIENDATARIO")
//    void crearSolicitud_RolInvalido_ThrowsException() {
//        // Arrange
//        usuarioValido.setRol("PROPIETARIO");
//        when(userServiceClient.getUserById(1L)).thenReturn(usuarioValido);
//
//        // Act & Assert
//        assertThatThrownBy(() -> service.crearSolicitud(solicitudDTO))
//                .isInstanceOf(BusinessValidationException.class)
//                .hasMessageContaining("Solo usuarios con rol ARRIENDATARIO");
//
//        verify(repository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Debe lanzar excepción cuando el usuario tiene 3 solicitudes activas")
//    void crearSolicitud_MaxSolicitudesActivas_ThrowsException() {
//        // Arrange
//        when(userServiceClient.getUserById(1L)).thenReturn(usuarioValido);
//        when(repository.countByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(3L);
//
//        // Act & Assert
//        assertThatThrownBy(() -> service.crearSolicitud(solicitudDTO))
//                .isInstanceOf(BusinessValidationException.class)
//                .hasMessageContaining("máximo permitido de solicitudes activas");
//
//        verify(repository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Debe lanzar excepción cuando ya existe solicitud pendiente para la propiedad")
//    void crearSolicitud_SolicitudDuplicada_ThrowsException() {
//        // Arrange
//        when(userServiceClient.getUserById(1L)).thenReturn(usuarioValido);
//        when(repository.countByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(0L);
//        when(repository.existsByUsuarioIdAndPropiedadIdAndEstado(1L, 1L, "PENDIENTE")).thenReturn(true);
//
//        // Act & Assert
//        assertThatThrownBy(() -> service.crearSolicitud(solicitudDTO))
//                .isInstanceOf(BusinessValidationException.class)
//                .hasMessageContaining("Ya existe una solicitud pendiente");
//
//        verify(repository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Debe lanzar excepción cuando la propiedad no existe")
//    void crearSolicitud_PropiedadNoExiste_ThrowsException() {
//        // Arrange
//        when(userServiceClient.getUserById(1L)).thenReturn(usuarioValido);
//        when(repository.countByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(0L);
//        when(repository.existsByUsuarioIdAndPropiedadIdAndEstado(1L, 1L, "PENDIENTE")).thenReturn(false);
//        when(propertyServiceClient.existsProperty(1L)).thenReturn(false);
//
//        // Act & Assert
//        assertThatThrownBy(() -> service.crearSolicitud(solicitudDTO))
//                .isInstanceOf(BusinessValidationException.class)
//                .hasMessageContaining("propiedad con ID 1 no existe");
//
//        verify(repository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Debe lanzar excepción cuando la propiedad no está disponible")
//    void crearSolicitud_PropiedadNoDisponible_ThrowsException() {
//        // Arrange
//        when(userServiceClient.getUserById(1L)).thenReturn(usuarioValido);
//        when(repository.countByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(0L);
//        when(repository.existsByUsuarioIdAndPropiedadIdAndEstado(1L, 1L, "PENDIENTE")).thenReturn(false);
//        when(propertyServiceClient.existsProperty(1L)).thenReturn(true);
//        when(propertyServiceClient.isPropertyAvailable(1L)).thenReturn(false);
//
//        // Act & Assert
//        assertThatThrownBy(() -> service.crearSolicitud(solicitudDTO))
//                .isInstanceOf(BusinessValidationException.class)
//                .hasMessageContaining("no está disponible");
//
//        verify(repository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Debe lanzar excepción cuando el usuario no tiene documentos aprobados")
//    void crearSolicitud_SinDocumentosAprobados_ThrowsException() {
//        // Arrange
//        when(userServiceClient.getUserById(1L)).thenReturn(usuarioValido);
//        when(repository.countByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(0L);
//        when(repository.existsByUsuarioIdAndPropiedadIdAndEstado(1L, 1L, "PENDIENTE")).thenReturn(false);
//        when(propertyServiceClient.existsProperty(1L)).thenReturn(true);
//        when(propertyServiceClient.isPropertyAvailable(1L)).thenReturn(true);
//        when(documentServiceClient.hasApprovedDocuments(1L)).thenReturn(false);
//
//        // Act & Assert
//        assertThatThrownBy(() -> service.crearSolicitud(solicitudDTO))
//                .isInstanceOf(BusinessValidationException.class)
//                .hasMessageContaining("documentos aprobados");
//
//        verify(repository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Debe listar todas las solicitudes")
//    void listarTodas_RetornaListado() {
//        // Arrange
//        SolicitudArriendo solicitud2 = new SolicitudArriendo();
//        solicitud2.setId(2L);
//        when(repository.findAll()).thenReturn(Arrays.asList(solicitudEntity, solicitud2));
//        when(modelMapper.map(any(SolicitudArriendo.class), eq(SolicitudArriendoDTO.class)))
//                .thenReturn(solicitudDTO);
//
//        // Act
//        List<SolicitudArriendoDTO> resultado = service.listarTodas(false);
//
//        // Assert
//        assertThat(resultado).hasSize(2);
//        verify(repository, times(1)).findAll();
//    }
//
//    @Test
//    @DisplayName("Debe obtener solicitud por ID")
//    void obtenerPorId_SolicitudExiste_RetornaSolicitud() {
//        // Arrange
//        when(repository.findById(1L)).thenReturn(Optional.of(solicitudEntity));
//        when(modelMapper.map(any(SolicitudArriendo.class), eq(SolicitudArriendoDTO.class)))
//                .thenReturn(solicitudDTO);
//
//        // Act
//        SolicitudArriendoDTO resultado = service.obtenerPorId(1L, false);
//
//        // Assert
//        assertThat(resultado).isNotNull();
//        verify(repository, times(1)).findById(1L);
//    }
//
//    @Test
//    @DisplayName("Debe lanzar excepción cuando solicitud no existe")
//    void obtenerPorId_SolicitudNoExiste_ThrowsException() {
//        // Arrange
//        when(repository.findById(1L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThatThrownBy(() -> service.obtenerPorId(1L, false))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessageContaining("Solicitud no encontrada con ID: 1");
//    }
//
//    @Test
//    @DisplayName("Debe obtener solicitudes por usuario")
//    void obtenerPorUsuario_RetornaListado() {
//        // Arrange
//        when(repository.findByUsuarioId(1L)).thenReturn(Arrays.asList(solicitudEntity));
//        when(modelMapper.map(any(SolicitudArriendo.class), eq(SolicitudArriendoDTO.class)))
//                .thenReturn(solicitudDTO);
//
//        // Act
//        List<SolicitudArriendoDTO> resultado = service.obtenerPorUsuario(1L);
//
//        // Assert
//        assertThat(resultado).hasSize(1);
//        verify(repository, times(1)).findByUsuarioId(1L);
//    }
//
//    @Test
//    @DisplayName("Debe actualizar estado de solicitud")
//    void actualizarEstado_EstadoValido_Success() {
//        // Arrange
//        when(repository.findById(1L)).thenReturn(Optional.of(solicitudEntity));
//        when(repository.save(any(SolicitudArriendo.class))).thenReturn(solicitudEntity);
//        when(modelMapper.map(any(SolicitudArriendo.class), eq(SolicitudArriendoDTO.class)))
//                .thenReturn(solicitudDTO);
//
//        // Act
//        SolicitudArriendoDTO resultado = service.actualizarEstado(1L, "ACEPTADA");
//
//        // Assert
//        assertThat(resultado).isNotNull();
//        verify(repository, times(1)).save(any(SolicitudArriendo.class));
//    }
//
//    @Test
//    @DisplayName("Debe lanzar excepción cuando estado es inválido")
//    void actualizarEstado_EstadoInvalido_ThrowsException() {
//        // Arrange
//        when(repository.findById(1L)).thenReturn(Optional.of(solicitudEntity));
//
//        // Act & Assert
//        assertThatThrownBy(() -> service.actualizarEstado(1L, "INVALIDO"))
//                .isInstanceOf(BusinessValidationException.class)
//                .hasMessageContaining("Estado inválido");
//
//        verify(repository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Debe permitir crear solicitud a usuario ADMIN")
//    void crearSolicitud_UsuarioAdmin_Success() {
//        // Arrange
//        usuarioValido.setRol("ADMIN");
//        when(userServiceClient.getUserById(1L)).thenReturn(usuarioValido);
//        when(repository.countByUsuarioIdAndEstado(1L, "PENDIENTE")).thenReturn(0L);
//        when(repository.existsByUsuarioIdAndPropiedadIdAndEstado(1L, 1L, "PENDIENTE")).thenReturn(false);
//        when(propertyServiceClient.existsProperty(1L)).thenReturn(true);
//        when(propertyServiceClient.isPropertyAvailable(1L)).thenReturn(true);
//        when(documentServiceClient.hasApprovedDocuments(1L)).thenReturn(true);
//        when(modelMapper.map(any(SolicitudArriendoDTO.class), eq(SolicitudArriendo.class)))
//                .thenReturn(solicitudEntity);
//        when(repository.save(any(SolicitudArriendo.class))).thenReturn(solicitudEntity);
//        when(modelMapper.map(any(SolicitudArriendo.class), eq(SolicitudArriendoDTO.class)))
//                .thenReturn(solicitudDTO);
//
//        // Act
//        SolicitudArriendoDTO resultado = service.crearSolicitud(solicitudDTO);
//
//        // Assert
//        assertThat(resultado).isNotNull();
//        verify(repository, times(1)).save(any(SolicitudArriendo.class));
//    }
//}
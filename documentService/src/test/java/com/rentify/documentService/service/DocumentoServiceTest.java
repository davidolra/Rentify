package com.rentify.documentService.service;

import com.rentify.documentService.client.UserServiceClient;
import com.rentify.documentService.dto.DocumentoDTO;
import com.rentify.documentService.dto.external.UsuarioDTO;
import com.rentify.documentService.exception.BusinessValidationException;
import com.rentify.documentService.exception.ResourceNotFoundException;
import com.rentify.documentService.model.Documento;
import com.rentify.documentService.model.Estado;
import com.rentify.documentService.model.TipoDocumento;
import com.rentify.documentService.repository.DocumentoRepository;
import com.rentify.documentService.repository.EstadoRepository;
import com.rentify.documentService.repository.TipoDocumentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para DocumentoService.
 * Usa Mockito para mockear dependencias.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de DocumentoService")
class DocumentoServiceTest {

    @Mock
    private DocumentoRepository documentoRepository;

    @Mock
    private EstadoRepository estadoRepository;

    @Mock
    private TipoDocumentoRepository tipoDocumentoRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DocumentoService documentoService;

    private UsuarioDTO usuarioValido;
    private DocumentoDTO documentoDTO;
    private Documento documentoEntity;
    private Estado estadoPendiente;
    private TipoDocumento tipoDNI;

    @BeforeEach
    void setUp() {
        // Usuario válido
        usuarioValido = new UsuarioDTO();
        usuarioValido.setId(1L);
        usuarioValido.setRol("ARRIENDATARIO");
        usuarioValido.setEstado("Activo");

        // Estado PENDIENTE
        estadoPendiente = Estado.builder()
                .id(1L)
                .nombre("PENDIENTE")
                .build();

        // Tipo DNI
        tipoDNI = TipoDocumento.builder()
                .id(1L)
                .nombre("DNI")
                .build();

        // DTO de documento
        documentoDTO = DocumentoDTO.builder()
                .nombre("DNI_Juan_Perez.pdf")
                .usuarioId(1L)
                .estadoId(1L)
                .tipoDocId(1L)
                .build();

        // Entidad documento
        documentoEntity = Documento.builder()
                .id(1L)
                .nombre("DNI_Juan_Perez.pdf")
                .fechaSubido(new Date())
                .usuarioId(1L)
                .estadoId(1L)
                .tipoDocId(1L)
                .build();
    }

    @Test
    @DisplayName("crearDocumento - Debe crear documento exitosamente cuando todos los datos son válidos")
    void crearDocumento_DatosValidos_Success() {
        // Arrange
        when(userServiceClient.getUserById(1L)).thenReturn(usuarioValido);
        when(documentoRepository.countByUsuarioId(1L)).thenReturn(0L);
        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estadoPendiente));
        when(tipoDocumentoRepository.findById(1L)).thenReturn(Optional.of(tipoDNI));
        when(modelMapper.map(any(DocumentoDTO.class), eq(Documento.class))).thenReturn(documentoEntity);
        when(documentoRepository.save(any(Documento.class))).thenReturn(documentoEntity);
        when(modelMapper.map(any(Documento.class), eq(DocumentoDTO.class))).thenReturn(documentoDTO);

        // Act
        DocumentoDTO resultado = documentoService.crearDocumento(documentoDTO);

        // Assert
        assertThat(resultado).isNotNull();
        verify(documentoRepository, times(1)).save(any(Documento.class));
        verify(userServiceClient, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("crearDocumento - Debe lanzar excepción cuando el usuario no existe")
    void crearDocumento_UsuarioNoExiste_ThrowsException() {
        // Arrange
        when(userServiceClient.getUserById(1L)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> documentoService.crearDocumento(documentoDTO))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("usuario con ID 1 no existe");

        verify(documentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("crearDocumento - Debe lanzar excepción cuando el usuario no tiene permisos")
    void crearDocumento_UsuarioSinPermisos_ThrowsException() {
        // Arrange
        usuarioValido.setRol("PROPIETARIO"); // Rol sin permisos para subir documentos
        when(userServiceClient.getUserById(1L)).thenReturn(usuarioValido);

        // Act & Assert
        assertThatThrownBy(() -> documentoService.crearDocumento(documentoDTO))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("no tiene permisos para subir documentos");

        verify(documentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("crearDocumento - Debe lanzar excepción cuando se alcanza el límite de documentos")
    void crearDocumento_LimiteAlcanzado_ThrowsException() {
        // Arrange
        when(userServiceClient.getUserById(1L)).thenReturn(usuarioValido);
        when(documentoRepository.countByUsuarioId(1L)).thenReturn(10L); // Límite alcanzado

        // Act & Assert
        assertThatThrownBy(() -> documentoService.crearDocumento(documentoDTO))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("máximo de 10 documentos permitidos");

        verify(documentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("crearDocumento - Debe lanzar excepción cuando el estado no existe")
    void crearDocumento_EstadoNoExiste_ThrowsException() {
        // Arrange
        when(userServiceClient.getUserById(1L)).thenReturn(usuarioValido);
        when(documentoRepository.countByUsuarioId(1L)).thenReturn(0L);
        when(estadoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> documentoService.crearDocumento(documentoDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("estado con ID 1 no existe");

        verify(documentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("crearDocumento - Debe lanzar excepción cuando el tipo de documento no existe")
    void crearDocumento_TipoDocNoExiste_ThrowsException() {
        // Arrange
        when(userServiceClient.getUserById(1L)).thenReturn(usuarioValido);
        when(documentoRepository.countByUsuarioId(1L)).thenReturn(0L);
        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estadoPendiente));
        when(tipoDocumentoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> documentoService.crearDocumento(documentoDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("tipo de documento con ID 1 no existe");

        verify(documentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("obtenerPorId - Debe retornar documento cuando existe")
    void obtenerPorId_DocumentoExiste_RetornaDocumento() {
        // Arrange
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(documentoEntity));
        when(modelMapper.map(any(Documento.class), eq(DocumentoDTO.class))).thenReturn(documentoDTO);

        // Act
        DocumentoDTO resultado = documentoService.obtenerPorId(1L, false);

        // Assert
        assertThat(resultado).isNotNull();
        verify(documentoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("obtenerPorId - Debe lanzar excepción cuando el documento no existe")
    void obtenerPorId_DocumentoNoExiste_ThrowsException() {
        // Arrange
        when(documentoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> documentoService.obtenerPorId(999L, false))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("documento con ID 999 no existe");
    }

    @Test
    @DisplayName("obtenerPorUsuario - Debe retornar lista de documentos del usuario")
    void obtenerPorUsuario_UsuarioConDocumentos_RetornaLista() {
        // Arrange
        when(userServiceClient.existsUser(1L)).thenReturn(true);
        when(documentoRepository.findByUsuarioId(1L)).thenReturn(List.of(documentoEntity));
        when(modelMapper.map(any(Documento.class), eq(DocumentoDTO.class))).thenReturn(documentoDTO);

        // Act
        List<DocumentoDTO> resultado = documentoService.obtenerPorUsuario(1L, false);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(documentoRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    @DisplayName("obtenerPorUsuario - Debe lanzar excepción cuando el usuario no existe")
    void obtenerPorUsuario_UsuarioNoExiste_ThrowsException() {
        // Arrange
        when(userServiceClient.existsUser(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> documentoService.obtenerPorUsuario(999L, false))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("usuario con ID 999 no existe");
    }

    @Test
    @DisplayName("actualizarEstado - Debe actualizar estado exitosamente")
    void actualizarEstado_DatosValidos_Success() {
        // Arrange
        Estado nuevoEstado = Estado.builder().id(2L).nombre("ACEPTADO").build();
        when(documentoRepository.findById(1L)).thenReturn(Optional.of(documentoEntity));
        when(estadoRepository.findById(2L)).thenReturn(Optional.of(nuevoEstado));
        when(documentoRepository.save(any(Documento.class))).thenReturn(documentoEntity);
        when(modelMapper.map(any(Documento.class), eq(DocumentoDTO.class))).thenReturn(documentoDTO);

        // Act
        DocumentoDTO resultado = documentoService.actualizarEstado(1L, 2L);

        // Assert
        assertThat(resultado).isNotNull();
        verify(documentoRepository, times(1)).save(any(Documento.class));
    }

    @Test
    @DisplayName("hasApprovedDocuments - Debe retornar true cuando hay documentos aprobados")
    void hasApprovedDocuments_ConDocumentosAprobados_RetornaTrue() {
        // Arrange
        Estado estadoAceptado = Estado.builder().id(2L).nombre("ACEPTADO").build();
        when(estadoRepository.findByNombre("ACEPTADO")).thenReturn(Optional.of(estadoAceptado));
        when(documentoRepository.countByUsuarioIdAndEstadoId(1L, 2L)).thenReturn(1L);

        // Act
        boolean resultado = documentoService.hasApprovedDocuments(1L);

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    @DisplayName("hasApprovedDocuments - Debe retornar false cuando no hay documentos aprobados")
    void hasApprovedDocuments_SinDocumentosAprobados_RetornaFalse() {
        // Arrange
        Estado estadoAceptado = Estado.builder().id(2L).nombre("ACEPTADO").build();
        when(estadoRepository.findByNombre("ACEPTADO")).thenReturn(Optional.of(estadoAceptado));
        when(documentoRepository.countByUsuarioIdAndEstadoId(1L, 2L)).thenReturn(0L);

        // Act
        boolean resultado = documentoService.hasApprovedDocuments(1L);

        // Assert
        assertThat(resultado).isFalse();
    }

    @Test
    @DisplayName("eliminarDocumento - Debe eliminar documento exitosamente")
    void eliminarDocumento_DocumentoExiste_Success() {
        // Arrange
        when(documentoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(documentoRepository).deleteById(1L);

        // Act
        documentoService.eliminarDocumento(1L);

        // Assert
        verify(documentoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarDocumento - Debe lanzar excepción cuando el documento no existe")
    void eliminarDocumento_DocumentoNoExiste_ThrowsException() {
        // Arrange
        when(documentoRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> documentoService.eliminarDocumento(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("documento con ID 999 no existe");

        verify(documentoRepository, never()).deleteById(any());
    }
}
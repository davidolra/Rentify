package com.rentify.documentService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentify.documentService.dto.DocumentoDTO;
import com.rentify.documentService.exception.BusinessValidationException;
import com.rentify.documentService.exception.ResourceNotFoundException;
import com.rentify.documentService.service.DocumentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para DocumentoController.
 * Usa MockMvc para simular requests HTTP.
 */
@WebMvcTest(DocumentoController.class)
@DisplayName("Tests de DocumentoController")
class DocumentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DocumentoService documentoService;

    private DocumentoDTO documentoDTO;

    @BeforeEach
    void setUp() {
        documentoDTO = DocumentoDTO.builder()
                .id(1L)
                .nombre("DNI_Juan_Perez.pdf")
                .fechaSubido(new Date())
                .usuarioId(1L)
                .estadoId(1L)
                .tipoDocId(1L)
                .estadoNombre("PENDIENTE")
                .tipoDocNombre("DNI")
                .build();
    }

    @Test
    @DisplayName("POST /api/documentos - Debe crear documento y retornar 201")
    void crearDocumento_DatosValidos_Returns201() throws Exception {
        // Arrange
        when(documentoService.crearDocumento(any(DocumentoDTO.class)))
                .thenReturn(documentoDTO);

        // Act & Assert
        mockMvc.perform(post("/api/documentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("DNI_Juan_Perez.pdf"))
                .andExpect(jsonPath("$.usuarioId").value(1));

        verify(documentoService, times(1)).crearDocumento(any(DocumentoDTO.class));
    }

    @Test
    @DisplayName("POST /api/documentos - Debe retornar 400 cuando faltan campos requeridos")
    void crearDocumento_CamposFaltantes_Returns400() throws Exception {
        // Arrange - Documento sin nombre (campo requerido)
        DocumentoDTO invalido = DocumentoDTO.builder()
                .usuarioId(1L)
                .estadoId(1L)
                .tipoDocId(1L)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/documentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());

        verify(documentoService, never()).crearDocumento(any());
    }

    @Test
    @DisplayName("POST /api/documentos - Debe retornar 400 cuando validación de negocio falla")
    void crearDocumento_ValidacionFalla_Returns400() throws Exception {
        // Arrange
        when(documentoService.crearDocumento(any(DocumentoDTO.class)))
                .thenThrow(new BusinessValidationException("Usuario no tiene permisos"));

        // Act & Assert
        mockMvc.perform(post("/api/documentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Usuario no tiene permisos"));
    }

    @Test
    @DisplayName("GET /api/documentos - Debe listar todos los documentos")
    void listarTodos_DeberiaRetornar200() throws Exception {
        // Arrange
        when(documentoService.listarTodos(false))
                .thenReturn(List.of(documentoDTO));

        // Act & Assert
        mockMvc.perform(get("/api/documentos")
                        .param("includeDetails", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(documentoService, times(1)).listarTodos(false);
    }

    @Test
    @DisplayName("GET /api/documentos/{id} - Debe retornar documento cuando existe")
    void obtenerPorId_DocumentoExiste_Returns200() throws Exception {
        // Arrange
        when(documentoService.obtenerPorId(1L, true))
                .thenReturn(documentoDTO);

        // Act & Assert
        mockMvc.perform(get("/api/documentos/1")
                        .param("includeDetails", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("DNI_Juan_Perez.pdf"));

        verify(documentoService, times(1)).obtenerPorId(1L, true);
    }

    @Test
    @DisplayName("GET /api/documentos/{id} - Debe retornar 404 cuando no existe")
    void obtenerPorId_DocumentoNoExiste_Returns404() throws Exception {
        // Arrange
        when(documentoService.obtenerPorId(999L, true))
                .thenThrow(new ResourceNotFoundException("Documento no encontrado"));

        // Act & Assert
        mockMvc.perform(get("/api/documentos/999")
                        .param("includeDetails", "true"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Documento no encontrado"));
    }

    @Test
    @DisplayName("GET /api/documentos/usuario/{usuarioId} - Debe retornar documentos del usuario")
    void obtenerPorUsuario_UsuarioConDocumentos_Returns200() throws Exception {
        // Arrange
        when(documentoService.obtenerPorUsuario(1L, true))
                .thenReturn(List.of(documentoDTO));

        // Act & Assert
        mockMvc.perform(get("/api/documentos/usuario/1")
                        .param("includeDetails", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].usuarioId").value(1));

        verify(documentoService, times(1)).obtenerPorUsuario(1L, true);
    }

    @Test
    @DisplayName("GET /api/documentos/usuario/{usuarioId} - Debe retornar 404 cuando usuario no existe")
    void obtenerPorUsuario_UsuarioNoExiste_Returns404() throws Exception {
        // Arrange
        when(documentoService.obtenerPorUsuario(999L, true))
                .thenThrow(new ResourceNotFoundException("Usuario no existe"));

        // Act & Assert
        mockMvc.perform(get("/api/documentos/usuario/999")
                        .param("includeDetails", "true"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/documentos/{id}/estado/{estadoId} - Debe actualizar estado")
    void actualizarEstado_DatosValidos_Returns200() throws Exception {
        // Arrange
        DocumentoDTO actualizado = DocumentoDTO.builder()
                .id(1L)
                .nombre("DNI_Juan_Perez.pdf")
                .usuarioId(1L)
                .estadoId(2L)
                .tipoDocId(1L)
                .estadoNombre("ACEPTADO")
                .build();

        when(documentoService.actualizarEstado(1L, 2L))
                .thenReturn(actualizado);

        // Act & Assert
        mockMvc.perform(patch("/api/documentos/1/estado/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoNombre").value("ACEPTADO"));

        verify(documentoService, times(1)).actualizarEstado(1L, 2L);
    }

    @Test
    @DisplayName("GET /api/documentos/usuario/{usuarioId}/aprobados - Debe verificar documentos aprobados")
    void hasApprovedDocuments_ConDocumentosAprobados_ReturnsTrue() throws Exception {
        // Arrange
        when(documentoService.hasApprovedDocuments(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/documentos/usuario/1/aprobados"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(documentoService, times(1)).hasApprovedDocuments(1L);
    }

    @Test
    @DisplayName("GET /api/documentos/usuario/{usuarioId}/aprobados - Debe retornar false sin documentos")
    void hasApprovedDocuments_SinDocumentosAprobados_ReturnsFalse() throws Exception {
        // Arrange
        when(documentoService.hasApprovedDocuments(2L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/documentos/usuario/2/aprobados"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("DELETE /api/documentos/{id} - Debe eliminar documento y retornar 204")
    void eliminarDocumento_DocumentoExiste_Returns204() throws Exception {
        // Arrange
        doNothing().when(documentoService).eliminarDocumento(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/documentos/1"))
                .andExpect(status().isNoContent());

        verify(documentoService, times(1)).eliminarDocumento(1L);
    }

    @Test
    @DisplayName("DELETE /api/documentos/{id} - Debe retornar 404 cuando documento no existe")
    void eliminarDocumento_DocumentoNoExiste_Returns404() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Documento no encontrado"))
                .when(documentoService).eliminarDocumento(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/documentos/999"))
                .andExpect(status().isNotFound());
    }
}
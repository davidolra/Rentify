package com.rentify.applicationService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentify.applicationService.dto.SolicitudArriendoDTO;
import com.rentify.applicationService.exception.BusinessValidationException;
import com.rentify.applicationService.exception.ResourceNotFoundException;
import com.rentify.applicationService.service.SolicitudArriendoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SolicitudController.class)
@DisplayName("Tests de integración para SolicitudController")
class SolicitudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SolicitudArriendoService service;

    private SolicitudArriendoDTO solicitudDTO;

    @BeforeEach
    void setUp() {
        solicitudDTO = SolicitudArriendoDTO.builder()
                .id(1L)
                .usuarioId(1L)
                .propiedadId(1L)
                .estado("PENDIENTE")
                .fechaSolicitud(new Date())
                .build();
    }

    @Test
    @DisplayName("POST /api/solicitudes - Crear solicitud exitosamente")
    void crearSolicitud_DeberiaRetornar201() throws Exception {
        // Given
        when(service.crearSolicitud(any(SolicitudArriendoDTO.class))).thenReturn(solicitudDTO);

        // When & Then
        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitudDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.propiedadId").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(service, times(1)).crearSolicitud(any(SolicitudArriendoDTO.class));
    }

    @Test
    @DisplayName("POST /api/solicitudes - Validación fallida")
    void crearSolicitud_DatosInvalidos_DeberiaRetornar400() throws Exception {
        // Given - DTO sin usuarioId (campo requerido)
        SolicitudArriendoDTO dtoInvalido = SolicitudArriendoDTO.builder()
                .propiedadId(1L)
                .build();

        // When & Then
        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/solicitudes - Listar todas")
    void listarTodas_DeberiaRetornar200ConLista() throws Exception {
        // Given
        List<SolicitudArriendoDTO> solicitudes = Arrays.asList(solicitudDTO);
        when(service.listarTodas(false)).thenReturn(solicitudes);

        // When & Then
        mockMvc.perform(get("/api/solicitudes")
                        .param("includeDetails", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(service, times(1)).listarTodas(false);
    }

    @Test
    @DisplayName("GET /api/solicitudes/{id} - Obtener por ID exitoso")
    void obtenerPorId_SolicitudExiste_DeberiaRetornar200() throws Exception {
        // Given
        when(service.obtenerPorId(1L, true)).thenReturn(solicitudDTO);

        // When & Then
        mockMvc.perform(get("/api/solicitudes/1")
                        .param("includeDetails", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1));

        verify(service, times(1)).obtenerPorId(1L, true);
    }

    @Test
    @DisplayName("GET /api/solicitudes/{id} - No encontrada")
    void obtenerPorId_SolicitudNoExiste_DeberiaRetornar404() throws Exception {
        // Given
        when(service.obtenerPorId(999L, true))
                .thenThrow(new ResourceNotFoundException("Solicitud no encontrada con ID: 999"));

        // When & Then
        mockMvc.perform(get("/api/solicitudes/999")
                        .param("includeDetails", "true"))
                .andExpect(status().isNotFound());

        verify(service, times(1)).obtenerPorId(999L, true);
    }

    @Test
    @DisplayName("GET /api/solicitudes/usuario/{usuarioId}")
    void obtenerPorUsuario_DeberiaRetornar200() throws Exception {
        // Given
        List<SolicitudArriendoDTO> solicitudes = Arrays.asList(solicitudDTO);
        when(service.obtenerPorUsuario(1L)).thenReturn(solicitudes);

        // When & Then
        mockMvc.perform(get("/api/solicitudes/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(service, times(1)).obtenerPorUsuario(1L);
    }

    @Test
    @DisplayName("GET /api/solicitudes/propiedad/{propiedadId}")
    void obtenerPorPropiedad_DeberiaRetornar200() throws Exception {
        // Given
        List<SolicitudArriendoDTO> solicitudes = Arrays.asList(solicitudDTO);
        when(service.obtenerPorPropiedad(1L)).thenReturn(solicitudes);

        // When & Then
        mockMvc.perform(get("/api/solicitudes/propiedad/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(service, times(1)).obtenerPorPropiedad(1L);
    }

    @Test
    @DisplayName("PATCH /api/solicitudes/{id}/estado - Actualizar estado exitoso")
    void actualizarEstado_DeberiaRetornar200() throws Exception {
        // Given
        solicitudDTO.setEstado("ACEPTADA");
        when(service.actualizarEstado(1L, "ACEPTADA")).thenReturn(solicitudDTO);

        // When & Then
        mockMvc.perform(patch("/api/solicitudes/1/estado")
                        .param("estado", "ACEPTADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACEPTADA"));

        verify(service, times(1)).actualizarEstado(1L, "ACEPTADA");
    }

    @Test
    @DisplayName("PATCH /api/solicitudes/{id}/estado - Estado inválido")
    void actualizarEstado_EstadoInvalido_DeberiaRetornar400() throws Exception {
        // Given
        when(service.actualizarEstado(1L, "INVALIDO"))
                .thenThrow(new BusinessValidationException("Estado inválido: INVALIDO"));

        // When & Then
        mockMvc.perform(patch("/api/solicitudes/1/estado")
                        .param("estado", "INVALIDO"))
                .andExpect(status().isBadRequest());

        verify(service, times(1)).actualizarEstado(1L, "INVALIDO");
    }
}
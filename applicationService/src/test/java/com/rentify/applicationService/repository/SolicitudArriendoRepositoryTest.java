package com.rentify.applicationService.repository;

import com.rentify.applicationService.model.SolicitudArriendo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests de integración para SolicitudArriendoRepository")
class SolicitudArriendoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SolicitudArriendoRepository repository;

    private SolicitudArriendo solicitud1;
    private SolicitudArriendo solicitud2;

    @BeforeEach
    void setUp() {
        solicitud1 = SolicitudArriendo.builder()
                .usuarioId(1L)
                .propiedadId(1L)
                .estado("PENDIENTE")
                .fechaSolicitud(new Date())
                .build();

        solicitud2 = SolicitudArriendo.builder()
                .usuarioId(1L)
                .propiedadId(2L)
                .estado("ACEPTADA")
                .fechaSolicitud(new Date())
                .build();

        entityManager.persist(solicitud1);
        entityManager.persist(solicitud2);
        entityManager.flush();
    }

    @Test
    @DisplayName("findByUsuarioId - Debería encontrar solicitudes del usuario")
    void findByUsuarioId_DeberiaRetornarSolicitudesDelUsuario() {
        // When
        List<SolicitudArriendo> solicitudes = repository.findByUsuarioId(1L);

        // Then
        assertThat(solicitudes).hasSize(2);
        assertThat(solicitudes).extracting(SolicitudArriendo::getUsuarioId)
                .containsOnly(1L);
    }

    @Test
    @DisplayName("findByUsuarioId - Usuario sin solicitudes")
    void findByUsuarioId_UsuarioSinSolicitudes_DeberiaRetornarListaVacia() {
        // When
        List<SolicitudArriendo> solicitudes = repository.findByUsuarioId(999L);

        // Then
        assertThat(solicitudes).isEmpty();
    }

    @Test
    @DisplayName("findByPropiedadId - Debería encontrar solicitudes de la propiedad")
    void findByPropiedadId_DeberiaRetornarSolicitudesDeLaPropiedad() {
        // When
        List<SolicitudArriendo> solicitudes = repository.findByPropiedadId(1L);

        // Then
        assertThat(solicitudes).hasSize(1);
        assertThat(solicitudes.get(0).getPropiedadId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findByPropiedadId - Propiedad sin solicitudes")
    void findByPropiedadId_PropiedadSinSolicitudes_DeberiaRetornarListaVacia() {
        // When
        List<SolicitudArriendo> solicitudes = repository.findByPropiedadId(999L);

        // Then
        assertThat(solicitudes).isEmpty();
    }

    @Test
    @DisplayName("save - Debería persistir solicitud correctamente")
    void save_DeberiaPersistirSolicitud() {
        // Given
        SolicitudArriendo nuevaSolicitud = SolicitudArriendo.builder()
                .usuarioId(2L)
                .propiedadId(3L)
                .estado("PENDIENTE")
                .fechaSolicitud(new Date())
                .build();

        // When
        SolicitudArriendo saved = repository.save(nuevaSolicitud);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId())).isPresent();
    }
}
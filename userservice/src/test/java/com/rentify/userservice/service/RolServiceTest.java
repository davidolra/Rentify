package com.rentify.userservice.service;

import com.rentify.userservice.model.Rol;
import com.rentify.userservice.repository.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    private Rol rol;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ADMIN");
    }

    @Test
    void crearRol_DeberiaGuardarRol() {
        when(rolRepository.save(any(Rol.class))).thenReturn(rol);

        Rol resultado = rolService.crearRol(rol);

        assertNotNull(resultado);
        assertEquals("ADMIN", resultado.getNombre());
        verify(rolRepository, times(1)).save(rol);
    }

    @Test
    void buscarPorId_DeberiaRetornarRolSiExiste() {
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        Optional<Rol> resultado = rolService.obtenerRolPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("ADMIN", resultado.get().getNombre());
    }
}

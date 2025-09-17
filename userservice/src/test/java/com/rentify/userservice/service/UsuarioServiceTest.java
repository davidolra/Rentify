package com.rentify.userservice.service;

import com.rentify.userservice.model.Usuario;
import com.rentify.userservice.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setPnombre("David");
        usuario.setEmail("david@test.com");
        usuario.setFnacimiento(LocalDate.of(1996, 11, 12));
    }

    @Test
    void registrarUsuario_DeberiaGuardarUsuario() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.registrarUsuario(usuario);

        assertNotNull(resultado);
        assertEquals("David", resultado.getPnombre());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void buscarPorId_DeberiaRetornarUsuarioSiExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("david@test.com", resultado.get().getEmail());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_DeberiaRetornarVacioSiNoExiste() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.buscarPorId(2L);

        assertFalse(resultado.isPresent());
        verify(usuarioRepository, times(1)).findById(2L);
    }
}

package com.rentify.userservice.service;

import com.rentify.userservice.dto.*;
import com.rentify.userservice.exception.AuthenticationException;
import com.rentify.userservice.exception.BusinessValidationException;
import com.rentify.userservice.exception.ResourceNotFoundException;
import com.rentify.userservice.model.Usuario;
import com.rentify.userservice.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolService rolService;

    @Mock
    private EstadoService estadoService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioDTO usuarioDTO;
    private Usuario usuarioEntity;
    private RolDTO rolDTO;
    private EstadoDTO estadoDTO;

    @BeforeEach
    void setUp() {
        usuarioDTO = UsuarioDTO.builder()
                .pnombre("Juan")
                .snombre("Carlos")
                .papellido("Pérez")
                .fnacimiento(LocalDate.of(1995, 5, 15))
                .email("juan.perez@email.com")
                .rut("12345678-9")
                .ntelefono("987654321")
                .clave("password123")
                .estadoId(1L)
                .rolId(3L)
                .build();

        usuarioEntity = Usuario.builder()
                .id(1L)
                .pnombre("Juan")
                .snombre("Carlos")
                .papellido("Pérez")
                .fnacimiento(LocalDate.of(1995, 5, 15))
                .email("juan.perez@email.com")
                .rut("12345678-9")
                .ntelefono("987654321")
                .duocVip(false)
                .clave("password123")
                .puntos(0)
                .codigoRef("ABC123XYZ")
                .fcreacion(LocalDate.now())
                .factualizacion(LocalDate.now())
                .estadoId(1L)
                .rolId(3L)
                .build();

        rolDTO = RolDTO.builder()
                .id(3L)
                .nombre("ARRIENDATARIO")
                .build();

        estadoDTO = EstadoDTO.builder()
                .id(1L)
                .nombre("ACTIVO")
                .build();
    }

    // ==================== TESTS DE REGISTRO ====================

    @Test
    @DisplayName("Debe registrar usuario exitosamente cuando todos los datos son válidos")
    void registrarUsuario_DatosValidos_Success() {
        // Arrange
        when(usuarioRepository.existsByEmail(usuarioDTO.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByRut(usuarioDTO.getRut())).thenReturn(false);
        when(usuarioRepository.existsByCodigoRef(anyString())).thenReturn(false);
        when(rolService.obtenerPorId(3L)).thenReturn(rolDTO);
        when(modelMapper.map(any(UsuarioDTO.class), eq(Usuario.class))).thenReturn(usuarioEntity);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEntity);
        when(modelMapper.map(any(Usuario.class), eq(UsuarioDTO.class))).thenReturn(usuarioDTO);
        when(rolService.obtenerPorId(3L)).thenReturn(rolDTO);
        when(estadoService.obtenerPorId(1L)).thenReturn(estadoDTO);

        // Act
        UsuarioDTO resultado = usuarioService.registrarUsuario(usuarioDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("juan.perez@email.com");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe detectar email DUOC y marcar como VIP")
    void registrarUsuario_EmailDuoc_MarcaComoVIP() {
        // Arrange
        usuarioDTO.setEmail("juan.perez@duoc.cl");
        Usuario usuarioVip = Usuario.builder()
                .id(1L)
                .email("juan.perez@duoc.cl")
                .duocVip(true)
                .pnombre("Juan")
                .snombre("Carlos")
                .papellido("Pérez")
                .fnacimiento(LocalDate.of(1995, 5, 15))
                .rut("12345678-9")
                .ntelefono("987654321")
                .clave("password123")
                .puntos(0)
                .codigoRef("ABC123XYZ")
                .fcreacion(LocalDate.now())
                .factualizacion(LocalDate.now())
                .estadoId(1L)
                .rolId(3L)
                .build();

        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByRut(anyString())).thenReturn(false);
        when(usuarioRepository.existsByCodigoRef(anyString())).thenReturn(false);
        when(rolService.obtenerPorId(3L)).thenReturn(rolDTO);
        when(modelMapper.map(any(UsuarioDTO.class), eq(Usuario.class))).thenReturn(usuarioVip);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioVip);
        when(modelMapper.map(any(Usuario.class), eq(UsuarioDTO.class))).thenReturn(usuarioDTO);
        when(rolService.obtenerPorId(3L)).thenReturn(rolDTO);
        when(estadoService.obtenerPorId(1L)).thenReturn(estadoDTO);

        // Act
        UsuarioDTO resultado = usuarioService.registrarUsuario(usuarioDTO);

        // Assert
        verify(usuarioRepository, times(1)).save(argThat(u -> u.getDuocVip().equals(true)));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario es menor de 18 años")
    void registrarUsuario_MenorDeEdad_ThrowsException() {
        // Arrange
        usuarioDTO.setFnacimiento(LocalDate.now().minusYears(17));

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.registrarUsuario(usuarioDTO))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("Debe ser mayor de 18 años");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el email ya está registrado")
    void registrarUsuario_EmailDuplicado_ThrowsException() {
        // Arrange
        when(usuarioRepository.existsByEmail(usuarioDTO.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.registrarUsuario(usuarioDTO))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("ya está registrado");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el RUT ya está registrado")
    void registrarUsuario_RutDuplicado_ThrowsException() {
        // Arrange
        when(usuarioRepository.existsByEmail(usuarioDTO.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByRut(usuarioDTO.getRut())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.registrarUsuario(usuarioDTO))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("RUT")
                .hasMessageContaining("ya está registrado");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe asignar rol ARRIENDATARIO por defecto si no se especifica")
    void registrarUsuario_SinRol_AsignaArriendatario() {
        // Arrange
        usuarioDTO.setRolId(null);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByRut(anyString())).thenReturn(false);
        when(usuarioRepository.existsByCodigoRef(anyString())).thenReturn(false);
        when(rolService.obtenerPorId(3L)).thenReturn(rolDTO);
        when(modelMapper.map(any(UsuarioDTO.class), eq(Usuario.class))).thenReturn(usuarioEntity);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEntity);
        when(modelMapper.map(any(Usuario.class), eq(UsuarioDTO.class))).thenReturn(usuarioDTO);
        when(estadoService.obtenerPorId(1L)).thenReturn(estadoDTO);

        // Act
        usuarioService.registrarUsuario(usuarioDTO);

        // Assert
        verify(rolService, times(1)).obtenerPorId(3L);
    }

    // ==================== TESTS DE LOGIN ====================

    @Test
    @DisplayName("Debe autenticar usuario exitosamente con credenciales válidas")
    void login_CredencialesValidas_Success() {
        // Arrange
        LoginDTO loginDTO = LoginDTO.builder()
                .email("juan.perez@email.com")
                .clave("password123")
                .build();

        when(usuarioRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(usuarioEntity));
        when(modelMapper.map(usuarioEntity, UsuarioDTO.class)).thenReturn(usuarioDTO);
        when(rolService.obtenerPorId(3L)).thenReturn(rolDTO);
        when(estadoService.obtenerPorId(1L)).thenReturn(estadoDTO);

        // Act
        UsuarioDTO resultado = usuarioService.login(loginDTO);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("juan.perez@email.com");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el email no existe")
    void login_EmailNoExiste_ThrowsException() {
        // Arrange
        LoginDTO loginDTO = LoginDTO.builder()
                .email("noexiste@email.com")
                .clave("password123")
                .build();

        when(usuarioRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.login(loginDTO))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Email o contraseña incorrectos");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la contraseña es incorrecta")
    void login_ClaveIncorrecta_ThrowsException() {
        // Arrange
        LoginDTO loginDTO = LoginDTO.builder()
                .email("juan.perez@email.com")
                .clave("wrongpassword")
                .build();

        when(usuarioRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(usuarioEntity));

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.login(loginDTO))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Email o contraseña incorrectos");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la cuenta está inactiva")
    void login_CuentaInactiva_ThrowsException() {
        // Arrange
        LoginDTO loginDTO = LoginDTO.builder()
                .email("juan.perez@email.com")
                .clave("password123")
                .build();

        usuarioEntity.setEstadoId(2L); // INACTIVO
        when(usuarioRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(usuarioEntity));

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.login(loginDTO))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("inactiva");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la cuenta está suspendida")
    void login_CuentaSuspendida_ThrowsException() {
        // Arrange
        LoginDTO loginDTO = LoginDTO.builder()
                .email("juan.perez@email.com")
                .clave("password123")
                .build();

        usuarioEntity.setEstadoId(3L); // SUSPENDIDO
        when(usuarioRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(usuarioEntity));

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.login(loginDTO))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("suspendida");
    }

    // ==================== TESTS DE CONSULTAS ====================

    @Test
    @DisplayName("Debe obtener todos los usuarios")
    void obtenerTodos_DeberiaRetornarListaDeUsuarios() {
        // Arrange
        Usuario usuario2 = Usuario.builder()
                .id(2L)
                .email("maria@email.com")
                .build();
        List<Usuario> usuarios = Arrays.asList(usuarioEntity, usuario2);

        when(usuarioRepository.findAll()).thenReturn(usuarios);
        when(modelMapper.map(any(Usuario.class), eq(UsuarioDTO.class))).thenReturn(usuarioDTO);

        // Act
        List<UsuarioDTO> resultado = usuarioService.obtenerTodos(false);

        // Assert
        assertThat(resultado).hasSize(2);
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener usuario por ID cuando existe")
    void obtenerPorId_UsuarioExiste_RetornaUsuario() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEntity));
        when(modelMapper.map(usuarioEntity, UsuarioDTO.class)).thenReturn(usuarioDTO);
        when(rolService.obtenerPorId(3L)).thenReturn(rolDTO);
        when(estadoService.obtenerPorId(1L)).thenReturn(estadoDTO);

        // Act
        UsuarioDTO resultado = usuarioService.obtenerPorId(1L, true);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("juan.perez@email.com");
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    void obtenerPorId_UsuarioNoExiste_ThrowsException() {
        // Arrange
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.obtenerPorId(999L, false))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario con ID 999 no encontrado");
    }

    @Test
    @DisplayName("Debe obtener usuario por email cuando existe")
    void obtenerPorEmail_UsuarioExiste_RetornaUsuario() {
        // Arrange
        when(usuarioRepository.findByEmail("juan.perez@email.com")).thenReturn(Optional.of(usuarioEntity));
        when(modelMapper.map(usuarioEntity, UsuarioDTO.class)).thenReturn(usuarioDTO);
        when(rolService.obtenerPorId(3L)).thenReturn(rolDTO);
        when(estadoService.obtenerPorId(1L)).thenReturn(estadoDTO);

        // Act
        UsuarioDTO resultado = usuarioService.obtenerPorEmail("juan.perez@email.com", true);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("juan.perez@email.com");
    }

    @Test
    @DisplayName("Debe obtener usuarios por rol")
    void obtenerPorRol_DeberiaRetornarUsuariosDelRol() {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(usuarioEntity);
        when(rolService.obtenerPorId(3L)).thenReturn(rolDTO);
        when(usuarioRepository.findByRolId(3L)).thenReturn(usuarios);
        when(modelMapper.map(any(Usuario.class), eq(UsuarioDTO.class))).thenReturn(usuarioDTO);

        // Act
        List<UsuarioDTO> resultado = usuarioService.obtenerPorRol(3L, false);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(rolService, times(1)).obtenerPorId(3L);
        verify(usuarioRepository, times(1)).findByRolId(3L);
    }

    @Test
    @DisplayName("Debe obtener usuarios VIP de DUOC")
    void obtenerUsuariosVIP_DeberiaRetornarUsuariosVIP() {
        // Arrange
        usuarioEntity.setDuocVip(true);
        List<Usuario> usuariosVip = Arrays.asList(usuarioEntity);
        when(usuarioRepository.findByDuocVip(true)).thenReturn(usuariosVip);
        when(modelMapper.map(any(Usuario.class), eq(UsuarioDTO.class))).thenReturn(usuarioDTO);

        // Act
        List<UsuarioDTO> resultado = usuarioService.obtenerUsuariosVIP(false);

        // Assert
        assertThat(resultado).hasSize(1);
        verify(usuarioRepository, times(1)).findByDuocVip(true);
    }

    // ==================== TESTS DE ACTUALIZACIÓN ====================

    @Test
    @DisplayName("Debe actualizar usuario correctamente")
    void actualizarUsuario_DatosValidos_Success() {
        // Arrange
        UsuarioDTO updateDTO = UsuarioDTO.builder()
                .pnombre("Juan Actualizado")
                .snombre("Carlos")
                .papellido("Pérez")
                .email("juan.perez@email.com")
                .ntelefono("999888777")
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEntity));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEntity);
        when(modelMapper.map(any(Usuario.class), eq(UsuarioDTO.class))).thenReturn(usuarioDTO);
        when(rolService.obtenerPorId(3L)).thenReturn(rolDTO);
        when(estadoService.obtenerPorId(1L)).thenReturn(estadoDTO);

        // Act
        UsuarioDTO resultado = usuarioService.actualizarUsuario(1L, updateDTO);

        // Assert
        assertThat(resultado).isNotNull();
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe cambiar rol de usuario correctamente")
    void cambiarRol_RolValido_Success() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEntity));
        when(rolService.obtenerPorId(2L)).thenReturn(rolDTO);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEntity);
        when(modelMapper.map(any(Usuario.class), eq(UsuarioDTO.class))).thenReturn(usuarioDTO);
        when(estadoService.obtenerPorId(1L)).thenReturn(estadoDTO);

        // Act
        UsuarioDTO resultado = usuarioService.cambiarRol(1L, 2L);

        // Assert
        assertThat(resultado).isNotNull();
        verify(rolService, times(1)).obtenerPorId(2L);
        verify(usuarioRepository, times(1)).save(argThat(u -> u.getRolId().equals(2L)));
    }

    @Test
    @DisplayName("Debe cambiar estado de usuario correctamente")
    void cambiarEstado_EstadoValido_Success() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEntity));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEntity);
        when(modelMapper.map(any(Usuario.class), eq(UsuarioDTO.class))).thenReturn(usuarioDTO);
        when(rolService.obtenerPorId(3L)).thenReturn(rolDTO);
        when(estadoService.obtenerPorId(2L)).thenReturn(estadoDTO);

        // Act
        UsuarioDTO resultado = usuarioService.cambiarEstado(1L, 2L);

        // Assert
        assertThat(resultado).isNotNull();
        verify(usuarioRepository, times(1)).save(argThat(u -> u.getEstadoId().equals(2L)));
    }

    @Test
    @DisplayName("Debe agregar puntos RentifyPoints correctamente")
    void agregarPuntos_PuntosValidos_Success() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEntity));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEntity);
        when(modelMapper.map(any(Usuario.class), eq(UsuarioDTO.class))).thenReturn(usuarioDTO);

        // Act
        UsuarioDTO resultado = usuarioService.agregarPuntos(1L, 100);

        // Assert
        assertThat(resultado).isNotNull();
        verify(usuarioRepository, times(1)).save(argThat(u -> u.getPuntos().equals(100)));
    }

    @Test
    @DisplayName("Debe verificar si usuario existe")
    void existeUsuario_UsuarioExiste_RetornaTrue() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean resultado = usuarioService.existeUsuario(1L);

        // Assert
        assertThat(resultado).isTrue();
        verify(usuarioRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("Debe verificar si usuario no existe")
    void existeUsuario_UsuarioNoExiste_RetornaFalse() {
        // Arrange
        when(usuarioRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean resultado = usuarioService.existeUsuario(999L);

        // Assert
        assertThat(resultado).isFalse();
        verify(usuarioRepository, times(1)).existsById(999L);
    }
}
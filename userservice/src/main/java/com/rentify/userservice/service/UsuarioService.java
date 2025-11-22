package com.rentify.userservice.service;

import com.rentify.userservice.constants.UserConstants.*;
import com.rentify.userservice.dto.UsuarioDTO;
import com.rentify.userservice.dto.LoginDTO;
import com.rentify.userservice.dto.RolDTO;
import com.rentify.userservice.dto.EstadoDTO;
import com.rentify.userservice.exception.AuthenticationException;
import com.rentify.userservice.exception.BusinessValidationException;
import com.rentify.userservice.exception.ResourceNotFoundException;
import com.rentify.userservice.model.Usuario;
import com.rentify.userservice.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de usuarios
 * Incluye registro, login, actualización y consultas
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolService rolService;
    private final EstadoService estadoService;
    private final ModelMapper modelMapper;

    /**
     * Registra un nuevo usuario en el sistema
     */
    @Transactional
    public UsuarioDTO registrarUsuario(UsuarioDTO usuarioDTO) {
        log.info("Registrando nuevo usuario: {}", usuarioDTO.getEmail());

        // 1. Validar edad mínima
        validarEdadMinima(usuarioDTO.getFnacimiento());

        // 2. Validar email único
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new BusinessValidationException(
                    String.format(Mensajes.EMAIL_DUPLICADO, usuarioDTO.getEmail())
            );
        }

        // 3. Validar RUT único
        if (usuarioRepository.existsByRut(usuarioDTO.getRut())) {
            throw new BusinessValidationException(
                    String.format(Mensajes.RUT_DUPLICADO, usuarioDTO.getRut())
            );
        }

        // 4. Generar código de referido único
        String codigoRef = generarCodigoReferido();
        usuarioDTO.setCodigoRef(codigoRef);

        // 5. Detectar si es correo DUOC (beneficio 20% descuento)
        boolean isDuocEmail = usuarioDTO.getEmail().toLowerCase().endsWith(Validaciones.DOMINIO_DUOC);
        usuarioDTO.setDuocVip(isDuocEmail);



        // 6. Establecer valores por defecto
        usuarioDTO.setPuntos(Validaciones.PUNTOS_INICIALES);
        usuarioDTO.setFcreacion(LocalDate.now());
        usuarioDTO.setFactualizacion(LocalDate.now());
        usuarioDTO.setEstadoId(1L); // Estado ACTIVO (ID 1) - hardcoded por ahora

        // 7. Si no tiene rol asignado, asignar ARRIENDATARIO por defecto
        if (usuarioDTO.getRolId() == null) {
            usuarioDTO.setRolId(3L); // ARRIENDATARIO
        }

        // 8. Validar que el rol existe
        rolService.obtenerPorId(usuarioDTO.getRolId());

        // 9. Guardar usuario
        // Mapeo manual sin ModelMapper para evitar problemas
        Usuario usuario = new Usuario();
        usuario.setPnombre(usuarioDTO.getPnombre());
        usuario.setSnombre(usuarioDTO.getSnombre());
        usuario.setPapellido(usuarioDTO.getPapellido());
        usuario.setFnacimiento(usuarioDTO.getFnacimiento());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setRut(usuarioDTO.getRut());
        usuario.setNtelefono(usuarioDTO.getNtelefono());
        usuario.setClave(usuarioDTO.getClave());
        usuario.setPuntos(usuarioDTO.getPuntos());
        usuario.setDuocVip(usuarioDTO.getDuocVip());
        usuario.setCodigoRef(usuarioDTO.getCodigoRef());
        usuario.setFcreacion(usuarioDTO.getFcreacion());
        usuario.setFactualizacion(usuarioDTO.getFactualizacion());
        usuario.setEstadoId(usuarioDTO.getEstadoId()); // ← Esto es crítico
        usuario.setRolId(usuarioDTO.getRolId());
        Usuario saved = usuarioRepository.save(usuario);

        log.info("Usuario registrado exitosamente con ID: {} - DUOC VIP: {}",
                saved.getId(), Boolean.valueOf(saved.getDuocVip()));

        return convertToDTO(saved, true);
    }

    /**
     * Autentica un usuario (login)
     */
    @Transactional(readOnly = true)
    public UsuarioDTO login(LoginDTO loginDTO) {
        log.info("Intento de login para email: {}", loginDTO.getEmail());

        // 1. Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new AuthenticationException(Mensajes.CREDENCIALES_INVALIDAS));

        // 2. Verificar contraseña (en producción usar BCrypt)
        if (!usuario.getClave().equals(loginDTO.getClave())) {
            log.warn("Intento de login fallido para email: {}", loginDTO.getEmail());
            throw new AuthenticationException(Mensajes.CREDENCIALES_INVALIDAS);
        }

        // 3. Verificar estado de la cuenta
        if (usuario.getEstadoId().equals(Estados.INACTIVO)) {
            throw new AuthenticationException(Mensajes.CUENTA_INACTIVA);
        }

        if (usuario.getEstadoId().equals(Estados.SUSPENDIDO)) {
            throw new AuthenticationException(Mensajes.CUENTA_SUSPENDIDA);
        }

        log.info("Login exitoso para usuario: {} (ID: {})", usuario.getEmail(), usuario.getId());
        return convertToDTO(usuario, true);
    }

    /**
     * Obtiene todos los usuarios
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodos(boolean includeDetails) {
        log.debug("Obteniendo todos los usuarios (includeDetails: {})", Boolean.valueOf(includeDetails));
        return usuarioRepository.findAll().stream()
                .map(u -> convertToDTO(u, includeDetails))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un usuario por su ID
     */
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorId(Long id, boolean includeDetails) {
        log.debug("Obteniendo usuario con ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(Mensajes.USUARIO_NO_ENCONTRADO, id)
                ));
        return convertToDTO(usuario, includeDetails);
    }

    /**
     * Obtiene un usuario por su email
     */
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorEmail(String email, boolean includeDetails) {
        log.debug("Obteniendo usuario con email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(Mensajes.USUARIO_EMAIL_NO_ENCONTRADO, email)
                ));
        return convertToDTO(usuario, includeDetails);
    }

    /**
     * Obtiene usuarios por rol
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerPorRol(Long rolId, boolean includeDetails) {
        log.debug("Obteniendo usuarios con rol ID: {}", rolId);
        // Validar que el rol existe
        rolService.obtenerPorId(rolId);

        return usuarioRepository.findByRolId(rolId).stream()
                .map(u -> convertToDTO(u, includeDetails))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene usuarios VIP de DUOC
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerUsuariosVIP(boolean includeDetails) {
        log.debug("Obteniendo usuarios DUOC VIP");
        return usuarioRepository.findByDuocVip(true).stream()
                .map(u -> convertToDTO(u, includeDetails))
                .collect(Collectors.toList());
    }

    /**
     * Actualiza los datos de un usuario
     */
    @Transactional
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        log.info("Actualizando usuario con ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(Mensajes.USUARIO_NO_ENCONTRADO, id)
                ));

        // Actualizar campos permitidos
        usuario.setPnombre(usuarioDTO.getPnombre());
        usuario.setSnombre(usuarioDTO.getSnombre());
        usuario.setPapellido(usuarioDTO.getPapellido());
        usuario.setNtelefono(usuarioDTO.getNtelefono());
        usuario.setFactualizacion(LocalDate.now());

        // Si se cambia el email, validar que no exista
        if (!usuario.getEmail().equals(usuarioDTO.getEmail())) {
            if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
                throw new BusinessValidationException(
                        String.format(Mensajes.EMAIL_DUPLICADO, usuarioDTO.getEmail())
                );
            }
            usuario.setEmail(usuarioDTO.getEmail());
            // Recalcular DUOC VIP si cambió el email
            boolean isDuocEmail = usuarioDTO.getEmail().toLowerCase().endsWith(Validaciones.DOMINIO_DUOC);
            usuario.setDuocVip(isDuocEmail);
        }

        Usuario updated = usuarioRepository.save(usuario);
        log.info("Usuario actualizado exitosamente: {}", updated.getId());

        return convertToDTO(updated, true);
    }

    /**
     * Cambia el rol de un usuario
     */
    @Transactional
    public UsuarioDTO cambiarRol(Long usuarioId, Long nuevoRolId) {
        log.info("Cambiando rol del usuario {} a rol {}", usuarioId, nuevoRolId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(Mensajes.USUARIO_NO_ENCONTRADO, usuarioId)
                ));

        // Validar que el rol existe
        rolService.obtenerPorId(nuevoRolId);

        usuario.setRolId(nuevoRolId);
        usuario.setFactualizacion(LocalDate.now());

        Usuario updated = usuarioRepository.save(usuario);
        log.info("Rol cambiado exitosamente para usuario: {}", updated.getId());

        return convertToDTO(updated, true);
    }

    /**
     * Cambia el estado de un usuario
     */
    @Transactional
    public UsuarioDTO cambiarEstado(Long usuarioId, Long nuevoEstadoId) {
        log.info("Cambiando estado del usuario {} a estado {}", usuarioId, nuevoEstadoId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(Mensajes.USUARIO_NO_ENCONTRADO, usuarioId)
                ));

        // Validar que el estado es válido
        if (!Estados.esValido(nuevoEstadoId)) {
            throw new BusinessValidationException(
                    String.format(Mensajes.ESTADO_INVALIDO, nuevoEstadoId)
            );
        }

        usuario.setEstadoId(nuevoEstadoId);
        usuario.setFactualizacion(LocalDate.now());

        Usuario updated = usuarioRepository.save(usuario);
        log.info("Estado cambiado exitosamente para usuario: {}", updated.getId());

        return convertToDTO(updated, true);
    }

    /**
     * Agrega puntos RentifyPoints a un usuario
     */
    @Transactional
    public UsuarioDTO agregarPuntos(Long usuarioId, Integer puntos) {
        log.info("Agregando {} puntos al usuario {}", puntos, usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(Mensajes.USUARIO_NO_ENCONTRADO, usuarioId)
                ));

        usuario.setPuntos(usuario.getPuntos() + puntos);
        usuario.setFactualizacion(LocalDate.now());

        Usuario updated = usuarioRepository.save(usuario);
        log.info("Puntos agregados exitosamente. Total: {}", updated.getPuntos());

        return convertToDTO(updated, false);
    }

    /**
     * Verifica si un usuario existe
     */
    @Transactional(readOnly = true)
    public boolean existeUsuario(Long id) {
        return usuarioRepository.existsById(id);
    }

    // ==================== MÉTODOS PRIVADOS ====================

    /**
     * Convierte una entidad Usuario a DTO
     */
    private UsuarioDTO convertToDTO(Usuario usuario, boolean includeDetails) {
        UsuarioDTO dto = modelMapper.map(usuario, UsuarioDTO.class);

        if (includeDetails) {
            try {
                if (usuario.getRolId() != null) {
                    RolDTO rol = rolService.obtenerPorId(usuario.getRolId());
                    dto.setRol(rol);
                }
            } catch (Exception e) {
                log.warn("No se pudo obtener información del rol {} para usuario {}",
                        usuario.getRolId(), usuario.getId());
            }

            try {
                EstadoDTO estado = estadoService.obtenerPorId(usuario.getEstadoId());
                dto.setEstado(estado);
            } catch (Exception e) {
                log.warn("No se pudo obtener información del estado {} para usuario {}",
                        usuario.getEstadoId(), usuario.getId());
            }
        }

        return dto;
    }

    /**
     * Valida que el usuario tenga la edad mínima requerida
     */
    private void validarEdadMinima(LocalDate fechaNacimiento) {
        Period edad = Period.between(fechaNacimiento, LocalDate.now());
        if (edad.getYears() < Validaciones.EDAD_MINIMA) {
            throw new BusinessValidationException(
                    String.format(Mensajes.EDAD_INSUFICIENTE, Integer.valueOf(Validaciones.EDAD_MINIMA))
            );
        }
    }

    /**
     * Genera un código de referido único de 9 caracteres
     * Formato: ABC123XYZ (3 letras + 3 números + 3 letras)
     */
    private String generarCodigoReferido() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder codigo = new StringBuilder();

        int intentos = 0;
        int maxIntentos = 10;

        do {
            codigo.setLength(0);
            for (int i = 0; i < Validaciones.LONGITUD_CODIGO_REF; i++) {
                codigo.append(caracteres.charAt(random.nextInt(caracteres.length())));
            }
            intentos++;

            if (intentos >= maxIntentos) {
                log.error("No se pudo generar un código de referido único después de {} intentos", maxIntentos);
                throw new BusinessValidationException(Mensajes.CODIGO_REF_DUPLICADO);
            }

        } while (usuarioRepository.existsByCodigoRef(codigo.toString()));

        return codigo.toString();
    }
}
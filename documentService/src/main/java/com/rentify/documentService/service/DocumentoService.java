package com.rentify.documentService.service;

import com.rentify.documentService.client.UserServiceClient;
import com.rentify.documentService.constants.DocumentConstants.*;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.rentify.documentService.constants.DocumentConstants.Mensajes;

/**
 * Servicio para gestión de documentos.
 * Implementa toda la lógica de negocio relacionada con documentos de usuarios.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentoService {

    // DEPENDENCIAS
    private final DocumentoRepository documentoRepository;
    private final EstadoRepository estadoRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final UserServiceClient userServiceClient;
    private final ModelMapper modelMapper;

    /**
     * Crea/sube un nuevo documento.
     *
     * @param documentoDTO DTO con los datos del documento
     * @return DocumentoDTO del documento creado
     */
    @Transactional
    public DocumentoDTO crearDocumento(DocumentoDTO documentoDTO) {
        log.info("Creando nuevo documento para usuario {} de tipo {}",
                documentoDTO.getUsuarioId(), documentoDTO.getTipoDocId());

        // 1. Validar que el usuario existe
        UsuarioDTO usuario = userServiceClient.getUserById(documentoDTO.getUsuarioId());
        if (usuario == null) {
            throw new BusinessValidationException(
                    String.format(Mensajes.USUARIO_NO_EXISTE, documentoDTO.getUsuarioId())
            );
        }

        // 2. Validar rol del usuario
        if (!Roles.puedeSubirDocumentos(usuario.getRol())) {
            throw new BusinessValidationException(
                    String.format(Mensajes.USUARIO_NO_PUEDE_SUBIR, usuario.getRol())
            );
        }

        // 3. Validar límite de documentos
        long cantidadDocumentos = documentoRepository.countByUsuarioId(documentoDTO.getUsuarioId());
        if (cantidadDocumentos >= Limites.MAX_DOCUMENTOS_POR_USUARIO) {
            throw new BusinessValidationException(
                    String.format(Mensajes.MAX_DOCUMENTOS_ALCANZADO, Limites.MAX_DOCUMENTOS_POR_USUARIO)
            );
        }

        // 4. Validar que el estado existe
        Estado estado = estadoRepository.findById(documentoDTO.getEstadoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(Mensajes.ESTADO_NO_ENCONTRADO, documentoDTO.getEstadoId())
                ));

        // 5. Validar que el tipo de documento existe
        TipoDocumento tipoDoc = tipoDocumentoRepository.findById(documentoDTO.getTipoDocId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(Mensajes.TIPO_DOC_NO_ENCONTRADO, documentoDTO.getTipoDocId())
                ));

        // 6. Crear y guardar documento
        Documento documento = modelMapper.map(documentoDTO, Documento.class);
        documento.setFechaSubido(new Date());

        Documento saved = documentoRepository.save(documento);
        log.info("Documento creado exitosamente con ID: {}", saved.getId());

        return convertToDTO(saved, true);
    }

    /**
     * Obtiene todos los documentos.
     *
     * @param includeDetails si debe incluir detalles expandidos
     * @return lista de DocumentoDTO
     */
    @Transactional(readOnly = true)
    public List<DocumentoDTO> listarTodos(boolean includeDetails) {
        log.debug("Listando todos los documentos (includeDetails: {})", Boolean.valueOf(includeDetails));

        return documentoRepository.findAll().stream()
                .map(d -> convertToDTO(d, includeDetails))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un documento por ID.
     *
     * @param id ID del documento
     * @param includeDetails si debe incluir detalles expandidos
     * @return DocumentoDTO encontrado
     */
    @Transactional(readOnly = true)
    public DocumentoDTO obtenerPorId(Long id, boolean includeDetails) {
        log.debug("Obteniendo documento con ID: {}", id);

        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(Mensajes.DOCUMENTO_NO_ENCONTRADO, id)
                ));

        return convertToDTO(documento, includeDetails);
    }

    /**
     * Obtiene todos los documentos de un usuario.
     *
     * @param usuarioId ID del usuario
     * @param includeDetails si debe incluir detalles expandidos
     * @return lista de DocumentoDTO
     */
    @Transactional(readOnly = true)
    public List<DocumentoDTO> obtenerPorUsuario(Long usuarioId, boolean includeDetails) {
        log.debug("Obteniendo documentos del usuario: {}", usuarioId);

        // Verificar que el usuario existe
        if (!userServiceClient.existsUser(usuarioId)) {
            throw new ResourceNotFoundException(
                    String.format(Mensajes.USUARIO_NO_EXISTE, usuarioId)
            );
        }

        List<Documento> documentos = documentoRepository.findByUsuarioId(usuarioId);

        if (documentos.isEmpty()) {
            log.warn("No se encontraron documentos para el usuario: {}", usuarioId);
        }

        return documentos.stream()
                .map(d -> convertToDTO(d, includeDetails))
                .collect(Collectors.toList());
    }

    /**
     * Actualiza el estado de un documento.
     *
     * @param documentoId ID del documento
     * @param nuevoEstadoId ID del nuevo estado
     * @return DocumentoDTO actualizado
     */
    @Transactional
    public DocumentoDTO actualizarEstado(Long documentoId, Long nuevoEstadoId) {
        log.info("Actualizando estado de documento {} a estado {}", documentoId, nuevoEstadoId);

        // 1. Verificar que el documento existe
        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(Mensajes.DOCUMENTO_NO_ENCONTRADO, documentoId)
                ));

        // 2. Verificar que el nuevo estado existe
        Estado nuevoEstado = estadoRepository.findById(nuevoEstadoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(Mensajes.ESTADO_NO_ENCONTRADO, nuevoEstadoId)
                ));

        // 3. Actualizar
        documento.setEstadoId(nuevoEstadoId);
        Documento updated = documentoRepository.save(documento);

        log.info("Estado de documento {} actualizado exitosamente a: {}",
                documentoId, nuevoEstado.getNombre());

        return convertToDTO(updated, true);
    }

    /**
     * Verifica si un usuario tiene documentos aprobados.
     * Útil para otros servicios como Application Service.
     *
     * @param usuarioId ID del usuario
     * @return true si tiene al menos un documento aprobado
     */
    @Transactional(readOnly = true)
    public boolean hasApprovedDocuments(Long usuarioId) {
        log.debug("Verificando documentos aprobados para usuario: {}", usuarioId);

        // Obtener el ID del estado ACEPTADO
        Estado estadoAceptado = estadoRepository.findByNombre(EstadoDocumento.ACEPTADO)
                .orElse(null);

        if (estadoAceptado == null) {
            log.warn("No existe estado ACEPTADO en la base de datos");
            return false;
        }

        long count = documentoRepository.countByUsuarioIdAndEstadoId(usuarioId, estadoAceptado.getId());
        return count > 0;
    }

    /**
     * Elimina un documento.
     *
     * @param id ID del documento a eliminar
     */
    @Transactional
    public void eliminarDocumento(Long id) {
        log.info("Eliminando documento con ID: {}", id);

        if (!documentoRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    String.format(Mensajes.DOCUMENTO_NO_ENCONTRADO, id)
            );
        }

        documentoRepository.deleteById(id);
        log.info("Documento {} eliminado exitosamente", id);
    }

    /**
     * Convierte una entidad Documento a DTO.
     *
     * @param documento entidad a convertir
     * @param includeDetails si debe incluir información expandida
     * @return DocumentoDTO
     */
    private DocumentoDTO convertToDTO(Documento documento, boolean includeDetails) {
        DocumentoDTO dto = modelMapper.map(documento, DocumentoDTO.class);

        if (includeDetails) {
            // Obtener nombre del estado
            try {
                Estado estado = estadoRepository.findById(documento.getEstadoId()).orElse(null);
                if (estado != null) {
                    dto.setEstadoNombre(estado.getNombre());
                }
            } catch (Exception e) {
                log.warn("No se pudo obtener información del estado {}: {}",
                        documento.getEstadoId(), e.getMessage());
            }

            // Obtener nombre del tipo de documento
            try {
                TipoDocumento tipoDoc = tipoDocumentoRepository.findById(documento.getTipoDocId()).orElse(null);
                if (tipoDoc != null) {
                    dto.setTipoDocNombre(tipoDoc.getNombre());
                }
            } catch (Exception e) {
                log.warn("No se pudo obtener información del tipo de documento {}: {}",
                        documento.getTipoDocId(), e.getMessage());
            }

            // Obtener información del usuario
            try {
                UsuarioDTO usuario = userServiceClient.getUserById(documento.getUsuarioId());
                dto.setUsuario(usuario);
            } catch (Exception e) {
                log.warn("No se pudo obtener información del usuario {}: {}",
                        documento.getUsuarioId(), e.getMessage());
            }
        }

        return dto;
    }
}
package com.rentify.documentService.service.impl;

import com.rentify.documentService.dto.DocumentRequest;
import com.rentify.documentService.dto.DocumentResponse;
import com.rentify.documentService.model.Document;
import com.rentify.documentService.model.Estado;
import com.rentify.documentService.model.DocumentType;
import com.rentify.documentService.repository.DocumentRepository;
import com.rentify.documentService.repository.EstadoRepository;
import com.rentify.documentService.repository.DocumentTypeRepository;
import com.rentify.documentService.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final EstadoRepository estadoRepository;
    private final DocumentTypeRepository documentTypeRepository;

    @Override
    public DocumentResponse uploadDocument(DocumentRequest request) {
        Estado estado = estadoRepository.findById(request.getEstadoId())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        DocumentType tipo = documentTypeRepository.findById(request.getTipoDocId())
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado"));

        Document document = Document.builder()
                .fechaSubido(new Date())
                .nombre(request.getNombre())
                .usuarioId(request.getUsuarioId())
                .estado(estado)
                .tipoDoc(tipo)
                .build();

        Document saved = documentRepository.save(document);
        return DocumentResponse.fromEntity(saved);
    }

    @Override
    public List<DocumentResponse> getDocumentsByUser(Long usuarioId) {
        return documentRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(DocumentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentResponse changeDocumentStatus(Long documentId, Long estadoId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        Estado estado = estadoRepository.findById(estadoId)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));

        document.setEstado(estado);
        Document updated = documentRepository.save(document);
        return DocumentResponse.fromEntity(updated);
    }
}

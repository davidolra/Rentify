package com.rentify.documentService.service;

import com.rentify.documentService.dto.DocumentRequest;
import com.rentify.documentService.dto.DocumentResponse;

import java.util.List;

public interface DocumentService {
    DocumentResponse uploadDocument(DocumentRequest request);
    List<DocumentResponse> getDocumentsByUser(Long usuarioId);
    DocumentResponse changeDocumentStatus(Long documentId, Long estadoId);
}

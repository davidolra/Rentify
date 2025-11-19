package com.rentify.documentService.controller;

import com.rentify.documentService.dto.DocumentRequest;
import com.rentify.documentService.dto.DocumentResponse;
import com.rentify.documentService.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentResponse> uploadDocument(@RequestBody DocumentRequest request) {
        return ResponseEntity.ok(documentService.uploadDocument(request));
    }

    @GetMapping("/user/{usuarioId}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByUser(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(documentService.getDocumentsByUser(usuarioId));
    }

    @PutMapping("/{documentId}/estado/{estadoId}")
    public ResponseEntity<DocumentResponse> changeDocumentStatus(
            @PathVariable Long documentId,
            @PathVariable Long estadoId) {
        return ResponseEntity.ok(documentService.changeDocumentStatus(documentId, estadoId));
    }
}

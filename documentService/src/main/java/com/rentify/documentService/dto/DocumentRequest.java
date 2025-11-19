package com.rentify.documentService.dto;

import lombok.Data;

@Data
public class DocumentRequest {
    private String nombre;
    private Long usuarioId;
    private Long estadoId;
    private Long tipoDocId;
}

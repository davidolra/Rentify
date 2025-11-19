package com.rentify.documentService.dto;

import com.rentify.documentService.model.Document;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class DocumentResponse {
    private Long id;
    private String nombre;
    private Date fechaSubido;
    private Long usuarioId;
    private String estado;
    private String tipoDoc;

    public static DocumentResponse fromEntity(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .nombre(document.getNombre())
                .fechaSubido(document.getFechaSubido())
                .usuarioId(document.getUsuarioId())
                .estado(document.getEstado().getNombre())
                .tipoDoc(document.getTipoDoc().getNombre())
                .build();
    }
}

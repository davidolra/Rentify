package com.rentify.documentService.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "documentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "f_subido", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaSubido;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(name = "usuarios_id", nullable = false)
    private Long usuarioId; // relación hacia otro microservicio

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "tipo_doc_id", nullable = false)
    private DocumentType tipoDoc;
}

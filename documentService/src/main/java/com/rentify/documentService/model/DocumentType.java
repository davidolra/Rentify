package com.rentify.documentService.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_doc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;
}

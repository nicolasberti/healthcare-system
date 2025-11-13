package com.healthcare.document_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "document")
public class Document {
    @Id
    private String id;
    private String name;
    private String extension;
    @Column(length = 1024)
    private String path;
    @Column(length = 2048)
    private String url; // esto no se deberia guardar en prod porque tiene credenciales, lo uso de ejemplo no mas para ver rutas reales de AWS S3
    private String contentType;
}
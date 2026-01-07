package com.healthcare.document_service.controller;

import com.healthcare.document_service.entity.URLDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.healthcare.document_service.entity.Document;
import com.healthcare.document_service.service.DocumentService;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService documentService;
    private final Resource resource;

    public DocumentController(DocumentService documentService, @Value("classpath:documents") Resource resource) {
        this.documentService = documentService;
        this.resource = resource;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        Document document = documentService.upload(file);
        return ResponseEntity.ok(document.getId());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable String id) throws IOException {
        byte[] document = documentService.download(id);
        Document docInfo = documentService.getDocumentInfo(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(docInfo.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + docInfo.getName() + "\"")
                .body(document);
    }

    @GetMapping("/{id}/url-firmada")
    public ResponseEntity<URLDto> getPath(@PathVariable String id) throws IOException {
        return ResponseEntity.ok(documentService.getUrl(id));
    }

    @GetMapping
    public ResponseEntity<List<Document>> list() {
        return ResponseEntity.ok(documentService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable String id) {
        documentService.delete(id);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }
}

package com.healthcare.notification_service.controller;

import com.healthcare.notification_service.model.ClassificationResponse;
import com.healthcare.notification_service.service.document.DocumentClassificationService;
import com.healthcare.notification_service.service.document.DocumentTextExtractorResolver;
import com.healthcare.notification_service.service.document.PdfTextExtractor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/ia")
@Slf4j
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentTextExtractorResolver extractorResolver;
    private final DocumentClassificationService classificationService;

    @PostMapping(
            value = "/clasificar/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ClassificationResponse> clasificar(
            @PathVariable String id,
            @RequestPart("file") MultipartFile file
    ) {
        log.info("Solicitud de clasificación para el documento {}", id);

        String text = extractorResolver.extractText(file);

        var response = classificationService.classify(id, text);

        return ResponseEntity.ok(response);
    }
}

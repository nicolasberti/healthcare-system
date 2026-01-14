package com.healthcare.notification_service.service;

import java.io.IOException;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.healthcare.notification_service.service.document.DocumentClassificationService;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Service
public class S3Service {
    private final S3Client s3Client;
    private final DocumentClassificationService classificationService;

    public S3Service(S3Client s3Client, DocumentClassificationService classificationService) {
        this.s3Client = s3Client;
        this.classificationService = classificationService;
    }

    public byte[] downloadFile(String bucket, String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> response =
                s3Client.getObject(request);

        try {
            return response.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo archivo de S3", e);
        }
    }

    // Descarga el archivo en base64 desde un bucket S3 y lo clasifica con la IA.
    public String classify(String bucket, String key) {
        String file = downloadFileAsBase64(bucket, key);
        return classificationService.classify(key, file).getClassification();
    }

    private String downloadFileAsBase64(String bucket, String key) {
        byte[] fileBytes = downloadFile(bucket, key);
        return Base64.getEncoder().encodeToString(fileBytes);
    }

}

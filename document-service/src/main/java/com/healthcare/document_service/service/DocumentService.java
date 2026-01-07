package com.healthcare.document_service.service;

import com.healthcare.document_service.config.AwsS3Properties;
import com.healthcare.document_service.entity.Document;
import com.healthcare.document_service.entity.URLDto;
import com.healthcare.document_service.repository.DocumentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final S3Client s3Client;
    private final AwsS3Properties s3Properties;
    private final S3Presigner s3Presigner;
    private final AwsS3Properties awsS3Properties;


    public URLDto getUrl(String id) {

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsS3Properties.bucket())   // claims ✅
                .key(document.getPath())            // documents/uuid_file.png
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .getObjectRequest(getObjectRequest)
                        .build();

        return new URLDto(
                s3Presigner.presignGetObject(presignRequest)
                        .url()
                        .toString()
        );
    }

    public Document upload(MultipartFile file) {
        String id = UUID.randomUUID().toString();
        String key = "documents/" + id + "_" + file.getOriginalFilename();

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(s3Properties.bucket())
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );

            Document document = Document.builder()
                    .id(id)
                    .name(file.getOriginalFilename())
                    .extension(getFileExtension(file))
                    .contentType(file.getContentType())
                    .path(key)
                    .url(generatePresignedUrl(key)) // opcional -> en prod no!!
                    .build();
            return documentRepository.save(document);

        } catch (IOException e) {
            throw new RuntimeException("Error uploading file to S3", e);
        }
    }

    private String getFileExtension(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isEmpty()) {
            return "";
        }

        int lastDotIndex = originalFileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return originalFileName.substring(lastDotIndex + 1).toLowerCase();
        }

        return "";
    }

    private String generatePresignedUrl(String key) {
        var presigner = S3Presigner.builder()
                .region(Region.of(s3Properties.region()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        s3Properties.accessKey(),
                                        s3Properties.secretKey()
                                )
                        )
                )
                .build();

        var getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Properties.bucket())
                .key(key)
                .build();

        var presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(getObjectRequest)
                .build();

        return presigner.presignGetObject(presignRequest).url().toString();
    }

    public byte[] download(String id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));

        String key = document.getPath();

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(s3Properties.bucket())
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes =
                    s3Client.getObjectAsBytes(getObjectRequest);

            return objectBytes.asByteArray();

        } catch (S3Exception e) {
            throw new RuntimeException("Error downloading file from S3: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    public Document getDocumentInfo(String id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
    }

    public List<Document> getAll() {
        return documentRepository.findAll();
    }

    public void delete(String id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));

        String key = document.getPath();

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3Properties.bucket())
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

            documentRepository.deleteById(id);

        } catch (S3Exception e) {
            throw new RuntimeException("Error deleting file from S3: " + e.awsErrorDetails().errorMessage(), e);
        }
    }
}

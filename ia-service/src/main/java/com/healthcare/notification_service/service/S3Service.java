package com.healthcare.notification_service.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;


import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;

import com.healthcare.notification_service.service.document.DocumentClassificationService;
import com.healthcare.notification_service.service.document.ImageTextExtractor;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

@Service
public class S3Service {
    private final S3Client s3Client;
    private final DocumentClassificationService classificationService;
    private final ImageTextExtractor imageTextExtractor;

    public S3Service(S3Client s3Client, DocumentClassificationService classificationService, ImageTextExtractor imageTextExtractor) {
        this.s3Client = s3Client;
        this.classificationService = classificationService;
        this.imageTextExtractor = imageTextExtractor;
    }

    public ResponseInputStream<GetObjectResponse> downloadFile(String bucket, String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3Client.getObject(request);
    }

    // Descarga el archivo en base64 desde un bucket S3 y lo clasifica con la IA.
    public String classify(String bucket, String key) {
        String file = downloadFileAsBase64(bucket, key);
        return classificationService.classify(key, file).getClassification();
    }

    private String downloadFileAsBase64(String bucket, String key) {
        ResponseInputStream<GetObjectResponse> file = downloadFile(bucket, key);
        try (InputStream is = file;
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            BufferedImage original = ImageIO.read(is);
            if (original == null) {
                throw new IllegalArgumentException("El archivo no es una imagen válida");
            }

            BufferedImage resized = Scalr.resize(
                    original,
                    Scalr.Method.QUALITY,
                    Scalr.Mode.AUTOMATIC,
                    256,
                    256
            );

            BufferedImage gray = new BufferedImage(
                    resized.getWidth(),
                    resized.getHeight(),
                    BufferedImage.TYPE_BYTE_GRAY
            );

            Graphics2D g = gray.createGraphics();
            g.drawImage(resized, 0, 0, null);
            g.dispose();

            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();

            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.7f);

            writer.setOutput(ImageIO.createImageOutputStream(baos));
            writer.write(null, new IIOImage(gray, null, null), param);
            writer.dispose();

            return Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Error procesando imagen para IA", e);
        }
    }

}

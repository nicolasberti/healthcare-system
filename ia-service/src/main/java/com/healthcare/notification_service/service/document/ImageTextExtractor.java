package com.healthcare.notification_service.service.document;

import java.util.Base64;

//import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageTextExtractor implements DocumentTextExtractor {
    public ImageTextExtractor() {
    }

    @Override
    public boolean supports(String contentType) {
        return contentType.startsWith("image/");
    }

    @Override
    public String extract(MultipartFile file) {
        try {
            return Base64.getEncoder().encodeToString(file.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo imagen", e);
        }
    }
}


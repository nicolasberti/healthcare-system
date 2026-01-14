package com.healthcare.notification_service.service.document;

import com.healthcare.notification_service.model.ClassificationResponse;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentClassificationService {
    private final ChatClient chatClient;
    private static final String PROMPT_TEMPLATE = """
        Clasificá el siguiente documento médico en UNA sola categoría:
        - DOCUMENTO GENERAL
        - RECETA
        - ESTUDIO MEDICO
        - SIN CLASIFICACION
        
        Tené en cuenta que el texto puede provenir de OCR y contener errores.
        
        Hay dos opciones cuando te pase el documento.
        - Te paso texto plano, porque era un PDF y lo extraje de ahí.
        - Te paso base64 porque era una imagen.
        
        Respondé SOLO con la categoría.
        
        Documento:
        %s
        """;
    public DocumentClassificationService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public ClassificationResponse classify(String documentId, String documentText){
        String prompt = String.format(PROMPT_TEMPLATE, documentText);

        log.info("Prompt a enviar: {}", prompt);
        String response = chatClient
                .prompt(prompt)
                .call()
                .content();
        log.info("Respuesta: {}", response);


        return new ClassificationResponse(documentId, response);
    }

}

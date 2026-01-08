package com.healthcare.security_service.repository;

import com.healthcare.security_service.model.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.time.Instant;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AuditEventRepository {

    private final DynamoDbClient dynamoDbClient;

    private static final String TABLE = "audit_events";

    public void save(AuditEvent event) {

        Map<String, AttributeValue> item = Map.of(
                "id", AttributeValue.fromS(event.getId()),
                "timestamp", AttributeValue.fromS(event.getTimestamp().toString()),
                "type", AttributeValue.fromS(event.getType()),
                "payload", AttributeValue.fromS(event.getPayload()),
                "version", AttributeValue.fromS(event.getVersion()),
                "createdAt", AttributeValue.fromS(Instant.now().toString())
        );

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }
}


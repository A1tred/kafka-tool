package com.isimplelab.kafkatool.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isimplelab.kafkatool.dto.SendMessageRequest;
import com.isimplelab.kafkatool.dto.SendMessagesResult;
import com.isimplelab.kafkatool.entity.AvroSchema;
import com.isimplelab.kafkatool.entity.MessageLog;
import com.isimplelab.kafkatool.repository.AvroSchemaRepository;
import com.isimplelab.kafkatool.repository.MessageLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class KafkaSendService {
    private final AvroSchemaRepository schemaRepository;
    private final MessageLogRepository logRepository;
    private final SchemaRegistryService schemaRegistryService;
    private final ObjectMapper objectMapper;
    private final AvroMessagePublisher rawPublisher;
    private final AvroMessagePublisher confluentPublisher;
    private final boolean useSchemaRegistry;

    public KafkaSendService(
            AvroSchemaRepository schemaRepository,
            MessageLogRepository logRepository,
            SchemaRegistryService schemaRegistryService,
            RawAvroMessagePublisher rawPublisher,
            ConfluentAvroMessagePublisher confluentPublisher,
            @Value("${kafka.schema-registry-url:}") String schemaRegistryUrl
    ) {
        this.schemaRepository = schemaRepository;
        this.logRepository = logRepository;
        this.schemaRegistryService = schemaRegistryService;
        this.objectMapper = new ObjectMapper();
        this.rawPublisher = rawPublisher;
        this.confluentPublisher = confluentPublisher;
        this.useSchemaRegistry = (schemaRegistryUrl != null && !schemaRegistryUrl.isBlank());
    }

    public SendMessagesResult sendMessages(SendMessageRequest request) {
        Schema avroSchema;
        AvroMessagePublisher publisher;
        String schemaName;

        if (request.getSchemaId() != null) {
            AvroSchema schemaEntity = schemaRepository.findById(request.getSchemaId())
                    .orElseThrow(() -> new IllegalArgumentException("Schema not found by id: " + request.getSchemaId()));
            avroSchema = new Schema.Parser().parse(schemaEntity.getSchemaJson());
            publisher = rawPublisher;
            schemaName = schemaEntity.getName();
        } else {
            String subject = request.getSchemaSubject();
            if ((subject == null || subject.isBlank()) && useSchemaRegistry && request.getTopic() != null && !request.getTopic().isBlank()) {
                subject = request.getTopic() + "-value";
            }
            if (subject != null && !subject.isBlank() && useSchemaRegistry) {
                avroSchema = schemaRegistryService.getSchemaBySubjectAndVersion(subject, request.getSchemaVersion());
                publisher = confluentPublisher;
                schemaName = subject;
            } else {
                throw new IllegalArgumentException("Укажи хотя бы один из параметров: schemaId, schemaSubject или topic (при активном Schema Registry)!");
            }
        }

        int successCount = 0;
        int failedCount = 0;
        List<String> errors = new ArrayList<>();
        String key = request.getKey();
        Map<String, String> headers = request.getHeaders();

        for (int i = 0; i < request.getMessageJsonList().size(); i++) {
            JsonNode messageNode = request.getMessageJsonList().get(i);
            MessageLog.MessageLogBuilder logBuilder = MessageLog.builder()
                    .topic(request.getTopic())
                    .schemaName(schemaName)
                    .timestamp(LocalDateTime.now());

            try {
                publisher.send(request.getTopic(), avroSchema, messageNode, key, headers);
                logBuilder.success(true).error(null);
                successCount++;
            } catch (Exception e) {
                String errorMsg = "Message #" + (i + 1) + ": " + e.getMessage();
                log.error(errorMsg, e);
                logBuilder.success(false).error(errorMsg);
                errors.add(errorMsg);
                failedCount++;
            } finally {
                try {
                    logBuilder.messageJson(messageNode.toString());
                } catch (Exception ex) {
                    logBuilder.messageJson("Ошибка сериализации для лога");
                }
                logBuilder.key(key);
                try {
                    logBuilder.headers(headers != null ? objectMapper.writeValueAsString(headers) : null);
                } catch (Exception ex) {
                    logBuilder.headers("Ошибка сериализации headers");
                }
                logRepository.save(logBuilder.build());
            }
        }

        return SendMessagesResult.builder()
                .total(request.getMessageJsonList().size())
                .success(successCount)
                .failed(failedCount)
                .errors(errors)
                .message("Успешно отправлено: " + successCount + " из " + request.getMessageJsonList().size())
                .build();
    }
}

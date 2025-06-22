package com.isimplelab.kafkatool.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SendMessageRequest {

    @Schema(description = "ID схемы (если используется локальная схема из БД)")
    private Long schemaId;

    @Schema(description = "Subject схемы из Registry (если используется Registry)")
    private String schemaSubject;

    @Schema(description = "Версия схемы из Registry (опционально)")
    private Integer schemaVersion;

    @NotBlank
    @Schema(description = "Kafka topic для отправки")
    private String topic;

    @Schema(description = "Список сообщений (валидных под Avro-схему)")
    private List<JsonNode> messageJsonList;

    @Schema(description = "Kafka message key (опционально)")
    private String key;

    @Schema(description = "Kafka headers (опционально)")
    private Map<String, String> headers;

    // Обновлённая ручная валидация
    public void validate() {
        // Разрешаем: schemaId, schemaSubject или topic (для автоопределения)
        if ((schemaId == null || schemaId == 0)
                && (schemaSubject == null || schemaSubject.isBlank())
                && (topic == null || topic.isBlank())) {
            throw new IllegalArgumentException("Укажи хотя бы один из параметров: schemaId, schemaSubject или topic (при активном Schema Registry)!");
        }
        if (messageJsonList == null || messageJsonList.isEmpty()) {
            throw new IllegalArgumentException("Необходимо передать хотя бы одно сообщение в messageJsonList");
        }
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Не указан topic");
        }
    }
}

package com.isimplelab.kafkatool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
public class GenerateAndSendRequest {
    private Long schemaId;
    private String schemaSubject;
    private Integer schemaVersion;
    @Schema(description = "Kafka topic для отправки")
    private String topic;
    @Schema(description = "Количество сообщений для генерации и отправки")
    private Integer count = 1;
    @Schema(description = "Фиксированные значения/шаблоны для некоторых полей (по имени)")
    private Map<String, Object> fieldOverrides;
    @Schema(description = "Kafka message key (опционально)")
    private String key;
    @Schema(description = "Kafka headers (опционально)")
    private Map<String, String> headers;
}

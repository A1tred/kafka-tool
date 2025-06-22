package com.isimplelab.kafkatool.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
public class GenerateMessageRequest {
    @Schema(description = "ID схемы (если используется локальная схема из БД)")
    private Long schemaId;

    @Schema(description = "Subject схемы из Registry (если используется Registry)")
    private String schemaSubject;

    @Schema(description = "Версия схемы из Registry (опционально)")
    private Integer schemaVersion;

    @Schema(description = "Kafka topic (используется для автоопределения схемы через Registry)")
    private String topic;

    @Schema(description = "Количество сообщений для генерации")
    private Integer count = 1;

    @Schema(description = "Фиксированные значения/шаблоны для некоторых полей (по имени)")
    private Map<String, Object> fieldOverrides;
}

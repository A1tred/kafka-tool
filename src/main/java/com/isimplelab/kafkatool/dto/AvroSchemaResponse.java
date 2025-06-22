package com.isimplelab.kafkatool.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AvroSchemaResponse {
    private Long id;
    private String name;
    private String description;
    private JsonNode schemaJson;
    private LocalDateTime createdAt;
}

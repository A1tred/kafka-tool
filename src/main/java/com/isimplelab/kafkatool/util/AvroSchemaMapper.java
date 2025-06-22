package com.isimplelab.kafkatool.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isimplelab.kafkatool.dto.AvroSchemaResponse;
import com.isimplelab.kafkatool.entity.AvroSchema;

public class AvroSchemaMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static AvroSchemaResponse toDto(AvroSchema entity) {
        JsonNode schemaJsonNode = null;
        try {
            schemaJsonNode = objectMapper.readTree(entity.getSchemaJson());
        } catch (Exception e) {
            // В случае ошибки вернём null или можно прокинуть ошибку
        }

        return AvroSchemaResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .schemaJson(schemaJsonNode)
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

package com.isimplelab.kafkatool.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AvroSchemaCreateRequest {
    @NotBlank
    private String name;
    private String description;

    @NotNull
    private JsonNode schemaJson;

    public boolean isSchemaJsonEmpty() {
        return schemaJson == null || schemaJson.isEmpty();
    }
}

package com.isimplelab.kafkatool.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.isimplelab.kafkatool.service.SchemaRegistryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schema-registry")
@RequiredArgsConstructor
public class SchemaRegistryController {

    private final SchemaRegistryService schemaRegistryService;

    // Получить список всех subject'ов
    @GetMapping("/subjects")
    public List<String> getSubjects() {
        return schemaRegistryService.getSubjects();
    }

    // Получить схему по subject (и по версии, если указана)
    @GetMapping("/subjects/{subject}")
    public JsonNode getSchema(@PathVariable String subject,
                              @RequestParam(required = false) Integer version) {
        return schemaRegistryService.getSchemaMetadata(subject, version);
    }

    // (Опционально) Получить все схемы сразу (может быть долго)
    @GetMapping("/subjects/full")
    public List<JsonNode> getAllSchemas() {
        return schemaRegistryService.getAllSchemas();
    }
}

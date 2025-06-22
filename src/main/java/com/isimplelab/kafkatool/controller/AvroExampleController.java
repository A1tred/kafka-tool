package com.isimplelab.kafkatool.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.isimplelab.kafkatool.repository.AvroSchemaRepository;
import com.isimplelab.kafkatool.service.AvroExampleGenerator;
import com.isimplelab.kafkatool.service.SchemaRegistryService;
import lombok.RequiredArgsConstructor;
import org.apache.avro.Schema;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/examples")
@RequiredArgsConstructor
public class AvroExampleController {
    private final AvroSchemaRepository schemaRepository;
    private final SchemaRegistryService schemaRegistryService;
    private final AvroExampleGenerator generator;

    // Пример по локальной схеме (из БД)
    @GetMapping("/schema/{id}")
    public JsonNode exampleById(@PathVariable Long id) {
        var schemaEntity = schemaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schema not found: " + id));
        Schema avroSchema = new Schema.Parser().parse(schemaEntity.getSchemaJson());
        return generator.generateExample(avroSchema);
    }

    // Пример по Registry-схеме
    @GetMapping("/subject/{subject}")
    public JsonNode exampleBySubject(@PathVariable String subject,
                                     @RequestParam(required = false) Integer version) {
        Schema avroSchema = schemaRegistryService.getSchemaBySubjectAndVersion(subject, version);
        return generator.generateExample(avroSchema);
    }
}

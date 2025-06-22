package com.isimplelab.kafkatool.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isimplelab.kafkatool.dto.GenerateMessageRequest;
import com.isimplelab.kafkatool.entity.AvroSchema;
import com.isimplelab.kafkatool.repository.AvroSchemaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.avro.Schema;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MessageGenerationService {
    private final AvroSchemaRepository avroSchemaRepository;
    private final SchemaRegistryService schemaRegistryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<JsonNode> generateMessages(GenerateMessageRequest request) {
        Schema avroSchema;
        if (request.getSchemaId() != null) {
            AvroSchema schemaEntity = avroSchemaRepository.findById(request.getSchemaId())
                    .orElseThrow(() -> new IllegalArgumentException("Schema not found by id: " + request.getSchemaId()));
            avroSchema = new Schema.Parser().parse(schemaEntity.getSchemaJson());
        } else {
            String subject = request.getSchemaSubject();
            if ((subject == null || subject.isBlank()) && request.getTopic() != null && !request.getTopic().isBlank()) {
                subject = request.getTopic() + "-value";
            }
            if (subject != null && !subject.isBlank()) {
                avroSchema = schemaRegistryService.getSchemaBySubjectAndVersion(subject, request.getSchemaVersion());
            } else {
                throw new IllegalArgumentException("Укажи хотя бы один из параметров: schemaId, schemaSubject или topic (при активном Schema Registry)!");
            }
        }

        int count = request.getCount() != null && request.getCount() > 0 ? request.getCount() : 1;
        List<JsonNode> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Map<String, Object> overrides = request.getFieldOverrides() != null ? request.getFieldOverrides() : Collections.emptyMap();
            Object message = generateExample(avroSchema, overrides, i);
            result.add(objectMapper.valueToTree(message));
        }
        return result;
    }

    // Рекурсивный генератор
    private Object generateExample(Schema schema, Map<String, Object> fieldOverrides, int idx) {
        switch (schema.getType()) {
            case RECORD:
                Map<String, Object> record = new LinkedHashMap<>();
                for (Schema.Field field : schema.getFields()) {
                    String fieldName = field.name();
                    if (fieldOverrides.containsKey(fieldName)) {
                        record.put(fieldName, fieldOverrides.get(fieldName));
                    } else {
                        record.put(fieldName, generateExample(field.schema(), fieldOverrides, idx));
                    }
                }
                return record;
            case ARRAY:
                return List.of(generateExample(schema.getElementType(), fieldOverrides, idx));
            case MAP:
                return Map.of("key", generateExample(schema.getValueType(), fieldOverrides, idx));
            case UNION:
                for (Schema s : schema.getTypes()) {
                    if (s.getType() != Schema.Type.NULL) {
                        Object value = generateExample(s, fieldOverrides, idx);
                        Map<String, Object> unionObj = new LinkedHashMap<>();
                        unionObj.put(s.getType().getName(), value);
                        return unionObj;
                    }
                }
                return null;
            case STRING:
                return "str_" + UUID.randomUUID().toString().substring(0, 8) + "_" + idx;
            case INT:
                return new Random().nextInt(1000) + idx;
            case LONG:
                return System.currentTimeMillis() + idx;
            case FLOAT:
                return new Random().nextFloat() * 100;
            case DOUBLE:
                return new Random().nextDouble() * 1000;
            case BOOLEAN:
                return idx % 2 == 0;
            case NULL:
                return null;
            case ENUM:
                return schema.getEnumSymbols().isEmpty() ? null : schema.getEnumSymbols().get(idx % schema.getEnumSymbols().size());
            case FIXED:
                return "AAAA";
            case BYTES:
                return Base64.getEncoder().encodeToString(("bytes_" + idx).getBytes());
            default:
                return null;
        }
    }
}

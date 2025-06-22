package com.isimplelab.kafkatool.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.Schema;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AvroExampleGenerator {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode generateExample(Schema schema) {
        Object example = generateValue(schema);
        return objectMapper.valueToTree(example);
    }

    private Object generateValue(Schema schema) {
        switch (schema.getType()) {
            case RECORD:
                Map<String, Object> record = new LinkedHashMap<>();
                for (Schema.Field field : schema.getFields()) {
                    record.put(field.name(), generateValue(field.schema()));
                }
                return record;
            case ARRAY:
                return List.of(generateValue(schema.getElementType()));
            case MAP:
                return Map.of("key", generateValue(schema.getValueType()));
            case UNION:
                // если union, ищем не null тип и оборачиваем результат в {"тип": value}
                for (Schema s : schema.getTypes()) {
                    if (s.getType() != Schema.Type.NULL) {
                        Object value = generateValue(s);
                        Map<String, Object> unionObj = new LinkedHashMap<>();
                        unionObj.put(s.getType().getName(), value);
                        return unionObj;
                    }
                }
                return null;
            case STRING:
                return "string";
            case INT:
                return 123;
            case LONG:
                return 123456789L;
            case FLOAT:
                return 1.23f;
            case DOUBLE:
                return 2.34;
            case BOOLEAN:
                return true;
            case NULL:
                return null;
            case ENUM:
                return schema.getEnumSymbols().isEmpty() ? null : schema.getEnumSymbols().get(0);
            case FIXED:
                return "AAAA";
            case BYTES:
                return "base64==";
            default:
                return null;
        }
    }

}

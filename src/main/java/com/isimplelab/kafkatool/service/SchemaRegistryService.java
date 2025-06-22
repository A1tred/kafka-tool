package com.isimplelab.kafkatool.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.apache.avro.Schema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchemaRegistryService {

    @Value("${kafka.schema-registry-url}")
    private String registryUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // Получить список subject'ов
    public List<String> getSubjects() {
        String url = registryUrl + "/subjects";
        return restTemplate.getForObject(url, List.class);
    }

    // Получить полную metadata схемы (тело, id, subject, version) по subject и (опционально) версии
    public JsonNode getSchemaMetadata(String subject, Integer version) {
        String url = (version == null)
                ? String.format("%s/subjects/%s/versions/latest", registryUrl, subject)
                : String.format("%s/subjects/%s/versions/%d", registryUrl, subject, version);
        try {
            return restTemplate.getForObject(url, JsonNode.class);
        } catch (HttpClientErrorException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new IllegalArgumentException("Ошибка обращения к Schema Registry: " + ex.getMessage());
        }
    }

    // Получить тело схемы как строку (для Avro Parser)
    public String getRawSchemaString(String subject, Integer version) {
        JsonNode meta = getSchemaMetadata(subject, version);
        return meta.get("schema").asText();
    }

    // Получить Avro-схему (объект)
    public Schema getSchemaBySubjectAndVersion(String subject, Integer version) {
        String schemaStr = getRawSchemaString(subject, version);
        return new Schema.Parser().parse(schemaStr);
    }

    // (Опционально) Получить все схемы сразу
    public List<JsonNode> getAllSchemas() {
        List<String> subjects = getSubjects();
        List<JsonNode> schemas = new ArrayList<>();
        for (String subject : subjects) {
            schemas.add(getSchemaMetadata(subject, null));
        }
        return schemas;
    }
}

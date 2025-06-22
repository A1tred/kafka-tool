package com.isimplelab.kafkatool.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isimplelab.kafkatool.dto.AvroSchemaCreateRequest;
import com.isimplelab.kafkatool.dto.AvroSchemaResponse;
import com.isimplelab.kafkatool.entity.AvroSchema;
import com.isimplelab.kafkatool.repository.AvroSchemaRepository;
import com.isimplelab.kafkatool.util.AvroSchemaMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvroSchemaService {
    private final AvroSchemaRepository repository;
    private final ObjectMapper objectMapper;

    public AvroSchemaService(AvroSchemaRepository repository) {
        this.repository = repository;
        this.objectMapper = new ObjectMapper();
    }

    @Transactional
    public AvroSchemaResponse create(AvroSchemaCreateRequest request) {
        String schemaJsonString;
        try {
            schemaJsonString = objectMapper.writeValueAsString(request.getSchemaJson());
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка сериализации schemaJson: " + e.getMessage());
        }

        AvroSchema entity = AvroSchema.builder()
                .name(request.getName())
                .description(request.getDescription())
                .schemaJson(schemaJsonString)
                .createdAt(LocalDateTime.now())
                .build();
        return AvroSchemaMapper.toDto(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<AvroSchemaResponse> findAll() {
        return repository.findAll().stream()
                .map(AvroSchemaMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AvroSchemaResponse findById(Long id) {
        return repository.findById(id)
                .map(AvroSchemaMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Не найдена схема с id: " + id));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

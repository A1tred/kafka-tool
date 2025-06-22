package com.isimplelab.kafkatool.controller;

import com.isimplelab.kafkatool.dto.AvroSchemaCreateRequest;
import com.isimplelab.kafkatool.dto.AvroSchemaResponse;
import com.isimplelab.kafkatool.service.AvroSchemaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/schemas")
public class AvroSchemaController {

    private final AvroSchemaService service;

    public AvroSchemaController(AvroSchemaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AvroSchemaResponse> create(@Valid @RequestBody AvroSchemaCreateRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<AvroSchemaResponse>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvroSchemaResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

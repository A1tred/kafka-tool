package com.isimplelab.kafkatool.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.isimplelab.kafkatool.dto.GenerateMessageRequest;
import com.isimplelab.kafkatool.service.MessageGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/generate")
@RequiredArgsConstructor
public class MessageGenerationController {
    private final MessageGenerationService generator;

    @PostMapping
    public List<JsonNode> generate(@RequestBody GenerateMessageRequest request) {
        return generator.generateMessages(request);
    }
}

package com.isimplelab.kafkatool.controller;

import com.isimplelab.kafkatool.dto.MessageLogResponse;
import com.isimplelab.kafkatool.service.MessageLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class MessageLogController {
    private final MessageLogService service;

    public MessageLogController(MessageLogService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MessageLogResponse>> list() {
        return ResponseEntity.ok(service.findAll());
    }
}

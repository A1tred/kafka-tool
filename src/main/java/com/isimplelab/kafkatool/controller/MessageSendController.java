package com.isimplelab.kafkatool.controller;

import com.isimplelab.kafkatool.dto.SendMessageRequest;
import com.isimplelab.kafkatool.dto.SendMessagesResult;
import com.isimplelab.kafkatool.service.KafkaSendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/messages")
public class MessageSendController {
    private final KafkaSendService kafkaSendService;

    public MessageSendController(KafkaSendService kafkaSendService) {
        this.kafkaSendService = kafkaSendService;
    }

    @PostMapping
    public ResponseEntity<SendMessagesResult> send(@Valid @RequestBody SendMessageRequest request) {
        request.validate();
        SendMessagesResult result = kafkaSendService.sendMessages(request);
        return ResponseEntity.ok(result);
    }
}

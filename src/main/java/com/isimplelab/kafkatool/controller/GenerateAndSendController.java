package com.isimplelab.kafkatool.controller;

import com.isimplelab.kafkatool.dto.GenerateAndSendRequest;
import com.isimplelab.kafkatool.dto.SendMessagesResult;
import com.isimplelab.kafkatool.service.GenerateAndSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/generate-send")
@RequiredArgsConstructor
public class GenerateAndSendController {
    private final GenerateAndSendService service;

    @PostMapping
    public SendMessagesResult generateAndSend(@RequestBody GenerateAndSendRequest request) {
        return service.generateAndSend(request);
    }
}

package com.isimplelab.kafkatool.controller;

import com.isimplelab.kafkatool.service.KafkaTopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class KafkaTopicController {
    private final KafkaTopicService topicService;

    @GetMapping
    public Set<String> listTopics() {
        return topicService.listTopics();
    }
}

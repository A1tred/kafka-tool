package com.isimplelab.kafkatool.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.isimplelab.kafkatool.dto.GenerateAndSendRequest;
import com.isimplelab.kafkatool.dto.SendMessagesResult;
import com.isimplelab.kafkatool.dto.SendMessageRequest;
import com.isimplelab.kafkatool.dto.GenerateMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenerateAndSendService {
    private final MessageGenerationService generator;
    private final KafkaSendService sender;

    public SendMessagesResult generateAndSend(GenerateAndSendRequest request) {
        // 1. Генерируем сообщения
        GenerateMessageRequest genReq = new GenerateMessageRequest();
        genReq.setSchemaId(request.getSchemaId());
        genReq.setSchemaSubject(request.getSchemaSubject());
        genReq.setSchemaVersion(request.getSchemaVersion());
        genReq.setCount(request.getCount());
        genReq.setFieldOverrides(request.getFieldOverrides());
        genReq.setTopic(request.getTopic()); // ОБЯЗАТЕЛЬНО!

        List<JsonNode> messages = generator.generateMessages(genReq);

        // 2. Отправляем их
        SendMessageRequest sendReq = new SendMessageRequest();
        sendReq.setSchemaId(request.getSchemaId());
        sendReq.setSchemaSubject(request.getSchemaSubject());
        sendReq.setSchemaVersion(request.getSchemaVersion());
        sendReq.setTopic(request.getTopic());
        sendReq.setMessageJsonList(messages);
        sendReq.setKey(request.getKey());
        sendReq.setHeaders(request.getHeaders());

        return sender.sendMessages(sendReq);
    }
}

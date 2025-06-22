package com.isimplelab.kafkatool.service;

import com.isimplelab.kafkatool.dto.MessageLogResponse;
import com.isimplelab.kafkatool.entity.MessageLog;
import com.isimplelab.kafkatool.repository.MessageLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageLogService {
    private final MessageLogRepository repository;

    public MessageLogService(MessageLogRepository repository) {
        this.repository = repository;
    }

    public List<MessageLogResponse> findAll() {
        return repository.findAll().stream()
                .map(log -> MessageLogResponse.builder()
                        .id(log.getId())
                        .topic(log.getTopic())
                        .schemaName(log.getSchemaName())
                        .timestamp(log.getTimestamp())
                        .success(log.isSuccess())
                        .error(log.getError())
                        .messageJson(log.getMessageJson())
                        .key(log.getKey())
                        .headers(log.getHeaders())
                        .build())
                .toList();
    }
}

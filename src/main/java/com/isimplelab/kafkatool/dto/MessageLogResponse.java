package com.isimplelab.kafkatool.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageLogResponse {
    private Long id;
    private String topic;
    private String schemaName;
    private LocalDateTime timestamp;
    private boolean success;
    private String error;
    private String messageJson;
    private String key;
    private String headers;
}

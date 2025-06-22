package com.isimplelab.kafkatool.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SendMessagesResult {
    private int total;
    private int success;
    private int failed;
    private List<String> errors;
    private String message;
}

package com.isimplelab.kafkatool.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "message_logs")
public class MessageLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    private String schemaName;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private boolean success;

    @Column(columnDefinition = "text")
    private String error;

    @Column(columnDefinition = "text")
    private String messageJson;

    // Новые поля:
    @Column(columnDefinition = "text")
    private String key;

    @Column(columnDefinition = "text")
    private String headers;
}

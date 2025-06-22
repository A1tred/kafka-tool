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
@Table(name = "avro_schemas")
public class AvroSchema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false, columnDefinition = "text")
    private String schemaJson; // Сохраняем как строку в БД!

    @Column(nullable = false)
    private LocalDateTime createdAt;
}

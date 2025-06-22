package com.isimplelab.kafkatool.repository;

import com.isimplelab.kafkatool.entity.AvroSchema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvroSchemaRepository extends JpaRepository<AvroSchema, Long> {
    Optional<AvroSchema> findByName(String name);
}

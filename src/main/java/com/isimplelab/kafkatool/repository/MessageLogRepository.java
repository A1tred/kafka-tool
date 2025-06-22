package com.isimplelab.kafkatool.repository;

import com.isimplelab.kafkatool.entity.MessageLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageLogRepository extends JpaRepository<MessageLog, Long> {
}

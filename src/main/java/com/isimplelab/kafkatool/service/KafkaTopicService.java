package com.isimplelab.kafkatool.service;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaTopicService {
    private final String bootstrapServers;

    public KafkaTopicService(@Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public Set<String> listTopics() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        try (AdminClient admin = AdminClient.create(props)) {
            return admin.listTopics(new ListTopicsOptions().listInternal(false)).names().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Ошибка получения списка топиков: " + e.getMessage(), e);
        }
    }
}

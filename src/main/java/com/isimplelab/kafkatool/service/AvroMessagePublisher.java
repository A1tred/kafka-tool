package com.isimplelab.kafkatool.service;

import org.apache.avro.Schema;

import java.util.Map;

public interface AvroMessagePublisher {
    void send(String topic,
              Schema avroSchema,
              Object jsonObject,
              String key,
              Map<String, String> headers) throws Exception;
}

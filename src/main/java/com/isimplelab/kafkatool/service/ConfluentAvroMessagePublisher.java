package com.isimplelab.kafkatool.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

@Component
public class ConfluentAvroMessagePublisher implements AvroMessagePublisher {
    private final KafkaProducer<String, Object> producer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ConfluentAvroMessagePublisher(
            @Value("${kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${kafka.schema-registry-url}") String schemaRegistryUrl
    ) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", KafkaAvroSerializer.class.getName());
        props.put("schema.registry.url", schemaRegistryUrl);
        this.producer = new KafkaProducer<>(props);
    }

    @Override
    public void send(String topic, Schema avroSchema, Object jsonObject, String key, Map<String, String> headers) throws Exception {
        String jsonString = objectMapper.writeValueAsString(jsonObject);
        Decoder decoder = DecoderFactory.get().jsonDecoder(avroSchema, jsonString);
        GenericDatumReader<GenericRecord> reader = new GenericDatumReader<>(avroSchema);
        GenericRecord record = reader.read(null, decoder);

        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topic, key, record);
        if (headers != null) {
            headers.forEach((k, v) -> producerRecord.headers().add(k, v.getBytes()));
        }
        producer.send(producerRecord).get();
    }
}

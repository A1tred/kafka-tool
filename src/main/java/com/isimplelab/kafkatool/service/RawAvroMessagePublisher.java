package com.isimplelab.kafkatool.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.generic.GenericData;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Properties;

@Component
public class RawAvroMessagePublisher implements AvroMessagePublisher {
    private final KafkaProducer<String, byte[]> producer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RawAvroMessagePublisher(@Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        this.producer = new KafkaProducer<>(props);
    }

    @Override
    public void send(String topic, Schema avroSchema, Object jsonObject, String key, Map<String, String> headers) throws Exception {
        String jsonString = objectMapper.writeValueAsString(jsonObject);
        Decoder decoder = DecoderFactory.get().jsonDecoder(avroSchema, jsonString);
        GenericDatumReader<GenericRecord> reader = new GenericDatumReader<>(avroSchema);
        GenericRecord record = reader.read(null, decoder);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> writer = new GenericData().createDatumWriter(avroSchema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(record, encoder);
        encoder.flush();

        byte[] avroBytes = out.toByteArray();

        ProducerRecord<String, byte[]> producerRecord = new ProducerRecord<>(topic, key, avroBytes);
        if (headers != null) {
            headers.forEach((k, v) -> producerRecord.headers().add(k, v.getBytes()));
        }
        producer.send(producerRecord).get();
    }
}

package com.orbsec.organizationservice.kafka.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbsec.organizationservice.avro.model.OrganizationChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

@Slf4j
public class EventSerializer implements Serializer<OrganizationChangeEvent> {
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, OrganizationChangeEvent data) {
        return new byte[0];
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String topic, Headers headers, OrganizationChangeEvent data) {
        log.info("Attempting to serialize OrganizationChangeEvent object");
        try {
            if (data == null) {
                return null;
            }
            mapper.addMixIn(OrganizationChangeEvent.class, IgnoreSchemaProperty.class);
            return mapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            log.error("Failed serializing OrganizationChangeEvent: {}", e.getMessage());
        }
        log.info("OrganizationChangeEvent serialized!");
        return Serializer.super.serialize(topic, headers, data);
    }

    @Override
    public void close() {
        Serializer.super.close();
    }
}

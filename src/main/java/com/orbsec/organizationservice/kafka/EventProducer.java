package com.orbsec.organizationservice.kafka;

import com.orbsec.organizationservice.avro.model.ChangeType;
import com.orbsec.organizationservice.avro.model.OrganizationChangeEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    private final KafkaTemplate<Integer, OrganizationChangeEvent> kafkaTemplate;
    private final NewTopic organizationTopic;

    @Autowired
    public EventProducer(KafkaTemplate<Integer, OrganizationChangeEvent> kafkaTemplate, NewTopic organizationTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.organizationTopic = organizationTopic;
    }

    public void publishNewEvent(String organizationId, ChangeType changeType, String eventDescription) {
        var changeEvent = new OrganizationChangeEvent(organizationId, changeType, eventDescription);
        kafkaTemplate.send(organizationTopic.name(), changeEvent);
    }
}

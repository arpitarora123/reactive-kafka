package com.example.reactivekafkaconsumerandproducer.config;

import com.example.reactivekafkaconsumerandproducer.dto.FakeConsumerDTO;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReactiveKafkaConsumerConfig {
    @Bean
    public ReceiverOptions<String, FakeConsumerDTO> kafkaReceiverOptions(@Value(value = "${FAKE_CONSUMER_DTO_TOPIC}") String topic, KafkaProperties kafkaProperties) {
        ReceiverOptions<String, FakeConsumerDTO> basicReceiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
        return basicReceiverOptions.subscription(Collections.singletonList(topic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, FakeConsumerDTO> reactiveKafkaConsumerTemplate(ReceiverOptions<String, FakeConsumerDTO> kafkaReceiverOptions) {
        return new ReactiveKafkaConsumerTemplate<String, FakeConsumerDTO>(kafkaReceiverOptions);
    }


    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer() {
        return new DeadLetterPublishingRecoverer(getEventKafkaTemplate(), (record, ex) -> new TopicPartition("error_topic_dlt", -1));
    }

    private KafkaOperations<String, Object> getEventKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfigs()));
    }

    Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }



}
package com.example.reactivekafkaconsumerandproducer.service;

import com.example.reactivekafkaconsumerandproducer.dto.FakeProducerDTO;
import com.example.reactivekafkaconsumerandproducer.entity.FakeProducerEntity;
import com.example.reactivekafkaconsumerandproducer.repository.FakeProducerDTORepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@Service
public class ReactiveProducerService {

    private final Logger log = LoggerFactory.getLogger(ReactiveProducerService.class);
    private final ReactiveKafkaProducerTemplate<String, FakeProducerDTO> reactiveKafkaProducerTemplate;

    @Autowired
    FakeProducerDTORepository fakeProducerDTORepository;

    @Value(value = "${FAKE_PRODUCER_DTO_TOPIC}")
    private String topic;

    public ReactiveProducerService(ReactiveKafkaProducerTemplate<String, FakeProducerDTO> reactiveKafkaProducerTemplate) {
        this.reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate;
    }

    @Transactional
    public void send(FakeProducerDTO fakeProducerDTO) {
        log.info("send to topic={}, {}={},", topic, FakeProducerDTO.class.getSimpleName(), fakeProducerDTO);

        // db commit
        FakeProducerEntity fakeProducerEntity = new FakeProducerEntity(fakeProducerDTO.getId());
        fakeProducerDTORepository.save(fakeProducerEntity);
        log.info("Saved FakeProducerEntity {}", fakeProducerEntity);
        reactiveKafkaProducerTemplate.send(topic, fakeProducerDTO)
                .doOnSuccess(senderResult -> log.info("sent {} offset : {}", fakeProducerDTO, senderResult.recordMetadata().offset()))
                .timeout(Duration.of(10, ChronoUnit.SECONDS))
                .doOnError(throwable -> {
                    log.error("Error publishing message {}", throwable.getMessage());
                    throw new RuntimeException(throwable.getMessage());
                })
                .doOnCancel(() -> {
                    log.error("Cancelled publishing message ");
                    throw new RuntimeException("Cancelled publishing message");
                })
                .doOnTerminate(() -> {
                    log.error("Terminating publishing message ");
                    throw new RuntimeException("Terminating publishing message");
                })
                .subscribe();
    }
}
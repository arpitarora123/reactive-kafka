package com.example.reactivekafkaconsumerandproducer.service;

import com.example.reactivekafkaconsumerandproducer.dto.FakeConsumerDTO;
import com.example.reactivekafkaconsumerandproducer.exception.ReceiverRecordException;
import com.example.reactivekafkaconsumerandproducer.repository.FakeConsumerDTORepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.retry.Retry;

import java.time.Duration;


@Service
public class ReactiveConsumerService implements CommandLineRunner {
    Logger log = LoggerFactory.getLogger(ReactiveConsumerService.class);

    @Autowired
    private final DeadLetterPublishingRecoverer deadLetterPublishingRecoverer;
    @Autowired
    FakeConsumerDTORepository fakeConsumerDTORepository;
    private final ReactiveKafkaConsumerTemplate<String, FakeConsumerDTO> reactiveKafkaConsumerTemplate;

    public ReactiveConsumerService(DeadLetterPublishingRecoverer deadLetterPublishingRecoverer, ReactiveKafkaConsumerTemplate<String, FakeConsumerDTO> reactiveKafkaConsumerTemplate) {
        this.deadLetterPublishingRecoverer = deadLetterPublishingRecoverer;
        this.reactiveKafkaConsumerTemplate = reactiveKafkaConsumerTemplate;
    }

    private Flux<ReceiverRecord<String, FakeConsumerDTO>> consumeFakeConsumerDTO() {
        return reactiveKafkaConsumerTemplate
                .receive()
                // .delayElements(Duration.ofSeconds(2L)) // BACKPRESSURE
                .doOnNext(consumerRecord -> {
                            log.info("received key={}, value={} from topic={}, offset={}",
                                    consumerRecord.key(),
                                    consumerRecord.value(),
                                    consumerRecord.topic(),
                                    consumerRecord.offset());
                            try {
                                FakeConsumerDTO fakeConsumerDTO = consumerRecord.value();
                                log.info("successfully consumed {}={}", FakeConsumerDTO.class.getSimpleName(), Integer.parseInt(fakeConsumerDTO.getId()));

                                consumerRecord.receiverOffset().acknowledge();
                            } catch (Exception ex) {
                                throw new ReceiverRecordException(consumerRecord, new RuntimeException(ex.getMessage()));
                            }
                        }
                )

//                .doOnNext(fakeConsumerDTO -> {
//                    try {
//                        log.info("successfully consumed {}={}", FakeConsumerDTO.class.getSimpleName(), Integer.parseInt(fakeConsumerDTO.getId()));
//                    } catch (Exception ex) {
//                        throw new ReceiverRecordException(fakeConsumerDTO, new RuntimeException("test"));
//                    }
//
//                })
//                .doOnError((throwable, record) -> {
//                    log.error("something bad happened while consuming : {}", throwable.getMessage());
//
//                    throw new ReceiverRecordException(new ConsumerRecord<>(throwable.getMessage()), new RuntimeException("test"));
//                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)).transientErrors(true))
                .onErrorContinue((e, record) -> {
                    ReceiverRecordException ex = (ReceiverRecordException) e;
                    log.error("Retries exhausted for ", ex);
                    log.info("Publishing to dead letter queue {}", ex.getRecord());
                    deadLetterPublishingRecoverer.accept(ex.getRecord(), ex);
                    log.info("Published to dead letter queue {}", ex.getRecord());
                    ex.getRecord().receiverOffset().acknowledge();
                })
                .repeat();
    }

    @Override
    public void run(String... args) {
        // we have to trigger consumption
        consumeFakeConsumerDTO().subscribe();
    }
}
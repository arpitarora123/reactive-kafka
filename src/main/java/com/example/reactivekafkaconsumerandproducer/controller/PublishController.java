package com.example.reactivekafkaconsumerandproducer.controller;

import com.example.reactivekafkaconsumerandproducer.dto.FakeProducerDTO;
import com.example.reactivekafkaconsumerandproducer.service.ReactiveProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Running two instances one on 8080 and another on 8081
 * call publish on 8080
 */
@RestController
@Slf4j
public class PublishController {

    @Autowired
    ReactiveProducerService reactiveProducerService;

    @PostMapping("/publish")
    public void publish(@RequestBody FakeProducerDTO fakeProducerDTO) {

        WebClient.builder().baseUrl("localhost:8081/call")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build()
                .post()
                .bodyValue(fakeProducerDTO)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();

        //reactiveProducerService.send(fakeProducerDTO);
    }

    @PostMapping("/call")
    public void call(@RequestBody FakeProducerDTO fakeProducerDTO) {
        reactiveProducerService.send(fakeProducerDTO);
    }
}

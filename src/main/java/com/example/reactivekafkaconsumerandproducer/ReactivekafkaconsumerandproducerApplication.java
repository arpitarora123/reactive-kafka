package com.example.reactivekafkaconsumerandproducer;

import com.example.reactivekafkaconsumerandproducer.dto.FakeProducerDTO;
import com.example.reactivekafkaconsumerandproducer.service.ReactiveConsumerService;
import com.example.reactivekafkaconsumerandproducer.service.ReactiveProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableAutoConfiguration
public class ReactivekafkaconsumerandproducerApplication {

	@Autowired
	private ReactiveProducerService reactiveProducerService;

	@Autowired
	private ReactiveConsumerService reactiveConsumerService;

	public static void main(String[] args) {
		SpringApplication.run(ReactivekafkaconsumerandproducerApplication.class, args);
	}

}
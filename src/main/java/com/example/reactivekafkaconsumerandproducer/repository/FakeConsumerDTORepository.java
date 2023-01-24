package com.example.reactivekafkaconsumerandproducer.repository;

import com.example.reactivekafkaconsumerandproducer.entity.FakeConsumerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FakeConsumerDTORepository extends JpaRepository<FakeConsumerEntity, String> {
}

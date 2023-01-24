package com.example.reactivekafkaconsumerandproducer.repository;

import com.example.reactivekafkaconsumerandproducer.entity.FakeProducerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FakeProducerDTORepository extends JpaRepository<FakeProducerEntity, String> {
}

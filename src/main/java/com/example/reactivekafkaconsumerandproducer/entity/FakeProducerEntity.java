package com.example.reactivekafkaconsumerandproducer.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@JsonRootName("FakeProducer")
@NoArgsConstructor
@Entity
public class FakeProducerEntity {
    @JsonProperty("id")
    @Id
    private String id;

    public FakeProducerEntity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FakeProducerDTO{" +
                "id='" + id + '\'' +
                '}';
    }
}
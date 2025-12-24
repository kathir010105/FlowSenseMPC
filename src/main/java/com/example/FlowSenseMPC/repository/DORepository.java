package com.example.FlowSenseMPC.repository;

import com.example.FlowSenseMPC.model.DOMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DORepository extends JpaRepository<DOMeasurement, Long> {
    List<DOMeasurement> findTop20BySensorIdOrderByTimestampDesc(String sensorId);

    List<DOMeasurement> findTop2BySensorIdOrderByTimestampDesc(String sensorId);
}

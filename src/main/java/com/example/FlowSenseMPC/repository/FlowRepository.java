package com.example.FlowSenseMPC.repository;

import com.example.FlowSenseMPC.model.FlowMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlowRepository extends JpaRepository<FlowMeasurement, Long> {
    List<FlowMeasurement> findTop20BySensorIdOrderByTimestampDesc(String sensorId);

    List<FlowMeasurement> findTop2BySensorIdOrderByTimestampDesc(String sensorId);
}

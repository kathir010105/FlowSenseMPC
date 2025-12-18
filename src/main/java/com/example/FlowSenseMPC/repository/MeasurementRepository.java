package com.example.FlowSenseMPC.repository;

import com.example.FlowSenseMPC.model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    List<Measurement> findTop20BySensorIdOrderByTimestampDesc(String sensorId);

    List<Measurement> findTop2BySensorIdOrderByTimestampDesc(String sensorId);
}

package com.example.FlowSenseMPC.repository;

import com.example.FlowSenseMPC.model.TemperatureMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemperatureRepository extends JpaRepository<TemperatureMeasurement, Long> {
    List<TemperatureMeasurement> findTop20BySensorIdOrderByTimestampDesc(String sensorId);

    List<TemperatureMeasurement> findTop2BySensorIdOrderByTimestampDesc(String sensorId);
}

package com.example.FlowSenseMPC.repository;

import com.example.FlowSenseMPC.model.TurbidityMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurbidityRepository extends JpaRepository<TurbidityMeasurement, Long> {
    List<TurbidityMeasurement> findTop20BySensorIdOrderByTimestampDesc(String sensorId);

    List<TurbidityMeasurement> findTop2BySensorIdOrderByTimestampDesc(String sensorId);
}

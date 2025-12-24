package com.example.FlowSenseMPC.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "flow_measurements")
public class FlowMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sensorId;
    private Double value; // m³/hr
    private Double controlInput;
    private LocalDateTime timestamp;

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getControlInput() {
        return controlInput;
    }

    public void setControlInput(Double controlInput) {
        this.controlInput = controlInput;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

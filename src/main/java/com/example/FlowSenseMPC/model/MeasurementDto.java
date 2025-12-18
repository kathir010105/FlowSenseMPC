package com.example.FlowSenseMPC.model;

public class MeasurementDto {
    private String sensorId;
    private Double value;

    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
}

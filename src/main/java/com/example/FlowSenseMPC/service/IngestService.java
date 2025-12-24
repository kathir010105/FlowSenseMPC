package com.example.FlowSenseMPC.service;

import com.example.FlowSenseMPC.model.*;
import com.example.FlowSenseMPC.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class IngestService {

    private final TurbidityRepository turbidityRepo;
    private final FlowRepository flowRepo;
    private final TemperatureRepository temperatureRepo;
    private final DORepository doRepo;
    private final ModelService modelService;

    public IngestService(TurbidityRepository turbidityRepo, FlowRepository flowRepo,
            TemperatureRepository temperatureRepo, DORepository doRepo,
            ModelService modelService) {
        this.turbidityRepo = turbidityRepo;
        this.flowRepo = flowRepo;
        this.temperatureRepo = temperatureRepo;
        this.doRepo = doRepo;
        this.modelService = modelService;
    }

    public void saveMeasurement(MeasurementDto dto) {
        String sensorId = dto.getSensorId();
        LocalDateTime now = LocalDateTime.now();

        if (sensorId.startsWith("turbidity_")) {
            TurbidityMeasurement m = new TurbidityMeasurement();
            m.setSensorId(sensorId);
            m.setValue(dto.getValue());
            m.setControlInput(null);
            m.setTimestamp(now);
            turbidityRepo.save(m);
        } else if (sensorId.startsWith("flow_")) {
            FlowMeasurement m = new FlowMeasurement();
            m.setSensorId(sensorId);
            m.setValue(dto.getValue());
            m.setControlInput(null);
            m.setTimestamp(now);
            flowRepo.save(m);
        } else if (sensorId.startsWith("temp_")) {
            TemperatureMeasurement m = new TemperatureMeasurement();
            m.setSensorId(sensorId);
            m.setValue(dto.getValue());
            m.setControlInput(null);
            m.setTimestamp(now);
            temperatureRepo.save(m);
        } else if (sensorId.startsWith("do_")) {
            DOMeasurement m = new DOMeasurement();
            m.setSensorId(sensorId);
            m.setValue(dto.getValue());
            m.setControlInput(null);
            m.setTimestamp(now);
            doRepo.save(m);
        }

        modelService.updateModel(sensorId, 0.0);
    }

    public List<MeasurementDto> getRecent(String sensorId) {
        List<MeasurementDto> result = new ArrayList<>();

        if (sensorId.startsWith("turbidity_")) {
            List<TurbidityMeasurement> data = turbidityRepo.findTop20BySensorIdOrderByTimestampDesc(sensorId);
            data.forEach(m -> {
                MeasurementDto dto = new MeasurementDto();
                dto.setSensorId(m.getSensorId());
                dto.setValue(m.getValue());
                dto.setTimestamp(m.getTimestamp());
                result.add(dto);
            });
        } else if (sensorId.startsWith("flow_")) {
            List<FlowMeasurement> data = flowRepo.findTop20BySensorIdOrderByTimestampDesc(sensorId);
            data.forEach(m -> {
                MeasurementDto dto = new MeasurementDto();
                dto.setSensorId(m.getSensorId());
                dto.setValue(m.getValue());
                dto.setTimestamp(m.getTimestamp());
                result.add(dto);
            });
        } else if (sensorId.startsWith("temp_")) {
            List<TemperatureMeasurement> data = temperatureRepo.findTop20BySensorIdOrderByTimestampDesc(sensorId);
            data.forEach(m -> {
                MeasurementDto dto = new MeasurementDto();
                dto.setSensorId(m.getSensorId());
                dto.setValue(m.getValue());
                dto.setTimestamp(m.getTimestamp());
                result.add(dto);
            });
        } else if (sensorId.startsWith("do_")) {
            List<DOMeasurement> data = doRepo.findTop20BySensorIdOrderByTimestampDesc(sensorId);
            data.forEach(m -> {
                MeasurementDto dto = new MeasurementDto();
                dto.setSensorId(m.getSensorId());
                dto.setValue(m.getValue());
                dto.setTimestamp(m.getTimestamp());
                result.add(dto);
            });
        }

        return result;
    }

    public void updateLatestMeasurementWithControl(String sensorId, double controlInput) {
        if (sensorId.startsWith("turbidity_")) {
            List<TurbidityMeasurement> latest = turbidityRepo.findTop2BySensorIdOrderByTimestampDesc(sensorId);
            if (!latest.isEmpty()) {
                TurbidityMeasurement m = latest.get(0);
                m.setControlInput(controlInput);
                turbidityRepo.save(m);
            }
        } else if (sensorId.startsWith("flow_")) {
            List<FlowMeasurement> latest = flowRepo.findTop2BySensorIdOrderByTimestampDesc(sensorId);
            if (!latest.isEmpty()) {
                FlowMeasurement m = latest.get(0);
                m.setControlInput(controlInput);
                flowRepo.save(m);
            }
        } else if (sensorId.startsWith("temp_")) {
            List<TemperatureMeasurement> latest = temperatureRepo.findTop2BySensorIdOrderByTimestampDesc(sensorId);
            if (!latest.isEmpty()) {
                TemperatureMeasurement m = latest.get(0);
                m.setControlInput(controlInput);
                temperatureRepo.save(m);
            }
        } else if (sensorId.startsWith("do_")) {
            List<DOMeasurement> latest = doRepo.findTop2BySensorIdOrderByTimestampDesc(sensorId);
            if (!latest.isEmpty()) {
                DOMeasurement m = latest.get(0);
                m.setControlInput(controlInput);
                doRepo.save(m);
            }
        }
    }
}

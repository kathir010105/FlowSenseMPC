package com.example.FlowSenseMPC.service;

import com.example.FlowSenseMPC.model.Measurement;
import com.example.FlowSenseMPC.model.MeasurementDto;
import com.example.FlowSenseMPC.repository.MeasurementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IngestService {

    private final MeasurementRepository repo;
    private final ModelService modelService;

    public IngestService(MeasurementRepository repo, ModelService modelService) {
        this.repo = repo;
        this.modelService = modelService;
    }

    public void saveMeasurement(MeasurementDto dto) {
        Measurement m = new Measurement();
        m.setSensorId(dto.getSensorId());
        m.setValue(dto.getValue());
        m.setControlInput(null); // No control input when manually sending measurement
        m.setTimestamp(LocalDateTime.now());
        repo.save(m);

        // Train the model with new measurement data
        modelService.updateModel(dto.getSensorId(), 0.0);
    }

    public void saveMeasurementWithControl(String sensorId, double value, double controlInput) {
        Measurement m = new Measurement();
        m.setSensorId(sensorId);
        m.setValue(value);
        m.setControlInput(controlInput);
        m.setTimestamp(LocalDateTime.now());
        repo.save(m);

        // Train the model with new measurement data
        modelService.updateModel(sensorId, controlInput);
    }

    public List<Measurement> getRecent(String sensorId) {
        return repo.findTop20BySensorIdOrderByTimestampDesc(sensorId);
    }

    public void updateLatestMeasurementWithControl(String sensorId, double controlInput) {
        List<Measurement> latest = repo.findTop2BySensorIdOrderByTimestampDesc(sensorId);
        if (!latest.isEmpty()) {
            Measurement m = latest.get(0);
            m.setControlInput(controlInput);
            repo.save(m);
        }
    }
}

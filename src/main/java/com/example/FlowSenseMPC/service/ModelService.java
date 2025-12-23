package com.example.FlowSenseMPC.service;


import com.example.FlowSenseMPC.model.Measurement;
import com.example.FlowSenseMPC.model.RLSModel;
import com.example.FlowSenseMPC.repository.MeasurementRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModelService {

    private final MeasurementRepository repo;
    private final Map<String, RLSModel> models = new HashMap<>();

    public ModelService(MeasurementRepository repo) {
        this.repo = repo;
    }

    public void updateModel(String sensorId, double lastU) {
        // Create a new RLS model for this sensor if it doesn't exist
        models.putIfAbsent(sensorId, new RLSModel());
        
        List<Measurement> data = repo.findTop20BySensorIdOrderByTimestampDesc(sensorId);
        if (data.size() < 3) return;

        double yk = data.get(0).getValue();
        double yk1 = data.get(1).getValue();
        double yk2 = data.get(2).getValue();

        double[] phi = { yk1, yk2, lastU };
        models.get(sensorId).update(phi, yk);
    }

    public double[] getParameters(String sensorId) {
        models.putIfAbsent(sensorId, new RLSModel());
        return models.get(sensorId).getTheta();
    }
}


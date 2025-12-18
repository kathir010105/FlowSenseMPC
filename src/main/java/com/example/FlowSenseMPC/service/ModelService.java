package com.example.FlowSenseMPC.service;


import com.example.FlowSenseMPC.model.Measurement;
import com.example.FlowSenseMPC.model.RLSModel;
import com.example.FlowSenseMPC.repository.MeasurementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelService {

    private final MeasurementRepository repo;
    private final RLSModel model = new RLSModel();

    public ModelService(MeasurementRepository repo) {
        this.repo = repo;
    }

    public void updateModel(String sensorId, double lastU) {
        List<Measurement> data = repo.findTop20BySensorIdOrderByTimestampDesc(sensorId);
        if (data.size() < 3) return;

        double yk = data.get(0).getValue();
        double yk1 = data.get(1).getValue();
        double yk2 = data.get(2).getValue();

        double[] phi = { yk1, yk2, lastU };
        model.update(phi, yk);
    }

    public double[] getParameters() {
        return model.getTheta();
    }
}


package com.example.FlowSenseMPC.service;

import com.example.FlowSenseMPC.model.*;
import com.example.FlowSenseMPC.repository.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModelService {

    private final TurbidityRepository turbidityRepo;
    private final FlowRepository flowRepo;
    private final TemperatureRepository temperatureRepo;
    private final DORepository doRepo;
    private final Map<String, RLSModel> models = new HashMap<>();

    public ModelService(TurbidityRepository turbidityRepo, FlowRepository flowRepo,
            TemperatureRepository temperatureRepo, DORepository doRepo) {
        this.turbidityRepo = turbidityRepo;
        this.flowRepo = flowRepo;
        this.temperatureRepo = temperatureRepo;
        this.doRepo = doRepo;
    }

    public void updateModel(String sensorId, double lastU) {
        models.putIfAbsent(sensorId, new RLSModel());

        List<?> data = null;
        if (sensorId.startsWith("turbidity_")) {
            data = turbidityRepo.findTop20BySensorIdOrderByTimestampDesc(sensorId);
        } else if (sensorId.startsWith("flow_")) {
            data = flowRepo.findTop20BySensorIdOrderByTimestampDesc(sensorId);
        } else if (sensorId.startsWith("temp_")) {
            data = temperatureRepo.findTop20BySensorIdOrderByTimestampDesc(sensorId);
        } else if (sensorId.startsWith("do_")) {
            data = doRepo.findTop20BySensorIdOrderByTimestampDesc(sensorId);
        }

        if (data == null || data.size() < 3)
            return;

        double yk = getValue(data.get(0));
        double yk1 = getValue(data.get(1));
        double yk2 = getValue(data.get(2));

        double[] phi = { yk1, yk2, lastU };
        models.get(sensorId).update(phi, yk);
    }

    public double[] getParameters(String sensorId) {
        models.putIfAbsent(sensorId, new RLSModel());
        return models.get(sensorId).getTheta();
    }

    private Double getValue(Object measurement) {
        if (measurement instanceof TurbidityMeasurement)
            return ((TurbidityMeasurement) measurement).getValue();
        if (measurement instanceof FlowMeasurement)
            return ((FlowMeasurement) measurement).getValue();
        if (measurement instanceof TemperatureMeasurement)
            return ((TemperatureMeasurement) measurement).getValue();
        if (measurement instanceof DOMeasurement)
            return ((DOMeasurement) measurement).getValue();
        return 0.0;
    }
}

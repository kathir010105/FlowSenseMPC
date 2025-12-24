package com.example.FlowSenseMPC.service;

import com.example.FlowSenseMPC.model.*;
import com.example.FlowSenseMPC.repository.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ControlService {

    private final TurbidityRepository turbidityRepo;
    private final FlowRepository flowRepo;
    private final TemperatureRepository temperatureRepo;
    private final DORepository doRepo;

    public ControlService(TurbidityRepository turbidityRepo, FlowRepository flowRepo,
            TemperatureRepository temperatureRepo, DORepository doRepo) {
        this.turbidityRepo = turbidityRepo;
        this.flowRepo = flowRepo;
        this.temperatureRepo = temperatureRepo;
        this.doRepo = doRepo;
    }

    public double predict(double[] theta, double yk, double yk1, double uk) {
        return theta[0] * yk + theta[1] * yk1 + theta[2] * uk;
    }

    public double computeControl(double reference, double predicted) {
        double Kp = 0.8; // tuning parameter
        return Kp * (reference - predicted);
    }

    public Map<String, Double> getLatestState(String sensorId) {
        Map<String, Double> state = new HashMap<>();

        if (sensorId.startsWith("turbidity_")) {
            List<TurbidityMeasurement> latest = turbidityRepo.findTop2BySensorIdOrderByTimestampDesc(sensorId);
            extractState(latest, state);
        } else if (sensorId.startsWith("flow_")) {
            List<FlowMeasurement> latest = flowRepo.findTop2BySensorIdOrderByTimestampDesc(sensorId);
            extractState(latest, state);
        } else if (sensorId.startsWith("temp_")) {
            List<TemperatureMeasurement> latest = temperatureRepo.findTop2BySensorIdOrderByTimestampDesc(sensorId);
            extractState(latest, state);
        } else if (sensorId.startsWith("do_")) {
            List<DOMeasurement> latest = doRepo.findTop2BySensorIdOrderByTimestampDesc(sensorId);
            extractState(latest, state);
        } else {
            state.put("yk", 0.0);
            state.put("yk1", 0.0);
            state.put("uk", 0.0);
        }

        return state;
    }

    private <T> void extractState(List<T> latest, Map<String, Double> state) {
        if (latest.size() >= 2) {
            Double yk = getValue(latest.get(0));
            Double yk1 = getValue(latest.get(1));
            Double uk = getControlInput(latest.get(1));

            state.put("yk", yk);
            state.put("yk1", yk1);
            state.put("uk", uk != null ? uk : 0.0);
        } else if (latest.size() == 1) {
            state.put("yk", getValue(latest.get(0)));
            state.put("yk1", 0.0);
            state.put("uk", 0.0);
        } else {
            state.put("yk", 0.0);
            state.put("yk1", 0.0);
            state.put("uk", 0.0);
        }
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

    private Double getControlInput(Object measurement) {
        if (measurement instanceof TurbidityMeasurement)
            return ((TurbidityMeasurement) measurement).getControlInput();
        if (measurement instanceof FlowMeasurement)
            return ((FlowMeasurement) measurement).getControlInput();
        if (measurement instanceof TemperatureMeasurement)
            return ((TemperatureMeasurement) measurement).getControlInput();
        if (measurement instanceof DOMeasurement)
            return ((DOMeasurement) measurement).getControlInput();
        return null;
    }
}

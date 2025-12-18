package com.example.FlowSenseMPC.service;

import com.example.FlowSenseMPC.model.Measurement;
import com.example.FlowSenseMPC.repository.MeasurementRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ControlService {

    private final MeasurementRepository repo;

    public ControlService(MeasurementRepository repo) {
        this.repo = repo;
    }

    public double predict(double[] theta, double yk, double yk1, double uk) {
        return theta[0] * yk + theta[1] * yk1 + theta[2] * uk;
    }

    public double computeControl(double reference, double predicted) {
        double Kp = 0.8; // tuning parameter
        return Kp * (reference - predicted);
    }

    public Map<String, Double> getLatestState(String sensorId) {
        List<Measurement> latest = repo.findTop2BySensorIdOrderByTimestampDesc(sensorId);

        Map<String, Double> state = new HashMap<>();

        if (latest.size() >= 2) {
            state.put("yk", latest.get(0).getValue()); // Most recent y(k)
            state.put("yk1", latest.get(1).getValue()); // Previous y(k-1)

            // Get u(k-1) from the previous measurement's control input
            Double lastControl = latest.get(1).getControlInput();
            state.put("uk", lastControl != null ? lastControl : 0.0);
        } else if (latest.size() == 1) {
            state.put("yk", latest.get(0).getValue());
            state.put("yk1", 0.0);
            state.put("uk", 0.0);
        } else {
            state.put("yk", 0.0);
            state.put("yk1", 0.0);
            state.put("uk", 0.0);
        }

        return state;
    }
}

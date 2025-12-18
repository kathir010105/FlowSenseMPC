package com.example.FlowSenseMPC.controller;

import com.example.FlowSenseMPC.service.ControlService;
import com.example.FlowSenseMPC.service.IngestService;
import com.example.FlowSenseMPC.service.ModelService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/control")
public class ControlController {

    private final ModelService modelService;
    private final ControlService controlService;
    private final IngestService ingestService;

    public ControlController(ModelService modelService, ControlService controlService, IngestService ingestService) {
        this.modelService = modelService;
        this.controlService = controlService;
        this.ingestService = ingestService;
    }

    @PostMapping("/compute")
    public Map<String, Double> compute(@RequestBody Map<String, Object> payload) {

        double reference = ((Number) payload.get("reference")).doubleValue();
        double yk = ((Number) payload.get("yk")).doubleValue();
        double yk1 = ((Number) payload.get("yk1")).doubleValue();
        double uk = ((Number) payload.get("uk")).doubleValue();
        String sensorId = (String) payload.getOrDefault("sensorId", "pH_sensor_1");

        double[] theta = modelService.getParameters();
        double predicted = controlService.predict(theta, yk, yk1, uk);
        double control = controlService.computeControl(reference, predicted);

        // Automatically save computed control value for simulation
        ingestService.updateLatestMeasurementWithControl(sensorId, control);

        Map<String, Double> result = new HashMap<>();
        result.put("predicted", predicted);
        result.put("control", control);

        return result;
    }

    @GetMapping("/latest-state/{sensorId}")
    public Map<String, Double> getLatestState(@PathVariable String sensorId) {
        return controlService.getLatestState(sensorId);
    }

}

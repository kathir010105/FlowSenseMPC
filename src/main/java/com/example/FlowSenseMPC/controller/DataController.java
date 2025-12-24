package com.example.FlowSenseMPC.controller;

import com.example.FlowSenseMPC.model.MeasurementDto;
import com.example.FlowSenseMPC.service.IngestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private final IngestService ingestService;

    public DataController(IngestService ingestService) {
        this.ingestService = ingestService;
    }

    @PostMapping("/measure")
    public ResponseEntity<Void> ingest(@RequestBody MeasurementDto dto) {
        ingestService.saveMeasurement(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recent/{sensorId}")
    public List<MeasurementDto> recent(@PathVariable String sensorId) {
        return ingestService.getRecent(sensorId);
    }
}

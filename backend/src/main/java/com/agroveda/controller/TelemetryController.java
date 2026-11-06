package com.agroveda.controller;

import com.agroveda.model.SoilTelemetry;
import com.agroveda.service.SoilSimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class TelemetryController {

    private final SoilSimulationService simulationService;

    public TelemetryController(SoilSimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @GetMapping({"/api/telemetry", "/telemetry"})
    public ResponseEntity<SoilTelemetry> getTelemetry() {
        return ResponseEntity.ok(simulationService.getCurrentTelemetry());
    }

    @PostMapping("/api/telemetry/override")
    public ResponseEntity<SoilTelemetry> overrideTelemetry(@RequestBody Map<String, Object> payload) {
        double moisture = Double.parseDouble(payload.getOrDefault("moisture", 48.0).toString());
        Double temp = payload.containsKey("temperature") ? Double.parseDouble(payload.get("temperature").toString()) : null;
        Double humidity = payload.containsKey("humidity") ? Double.parseDouble(payload.get("humidity").toString()) : null;
        Double ph = payload.containsKey("soilPH") ? Double.parseDouble(payload.get("soilPH").toString()) : null;

        simulationService.setManualOverride(moisture, temp, humidity, ph);
        return ResponseEntity.ok(simulationService.getCurrentTelemetry());
    }

    @PostMapping("/api/telemetry/reset")
    public ResponseEntity<SoilTelemetry> resetTelemetry() {
        simulationService.resetToAutomaticSimulation();
        return ResponseEntity.ok(simulationService.getCurrentTelemetry());
    }
}

package com.agroveda.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "AgroVeda AI Java Backend",
                "engine", "Java 21 (Spring Boot 3.3.5)",
                "timestamp", LocalDateTime.now().toString(),
                "mode", "Agronomic Cloud & Intelligent Soil Simulation"
        ));
    }
}

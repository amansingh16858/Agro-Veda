package com.agroveda.controller;

import com.agroveda.model.*;
import com.agroveda.service.AgronomicReasoningService;
import com.agroveda.service.CropVisionService;
import com.agroveda.service.SoilSimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DiagnosticController {

    private final CropVisionService visionService;
    private final AgronomicReasoningService reasoningService;
    private final SoilSimulationService soilSimulationService;

    public DiagnosticController(
            CropVisionService visionService,
            AgronomicReasoningService reasoningService,
            SoilSimulationService soilSimulationService
    ) {
        this.visionService = visionService;
        this.reasoningService = reasoningService;
        this.soilSimulationService = soilSimulationService;
    }

    @PostMapping("/diagnose")
    public ResponseEntity<DiagnosticResponse> runFullDiagnostic(
            @RequestBody DiagnosticRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String clientKey
    ) {
        // Stage 1: Vision Extraction
        VisionAnalysisResult visionResult = visionService.analyzeLeaf(
                request.imageBase64(),
                request.cropHint(),
                clientKey
        );

        // Stage 2: Telemetry Correlation
        SoilTelemetry telemetry = (request.telemetry() != null)
                ? request.telemetry()
                : soilSimulationService.getCurrentTelemetry();

        // Stage 3: Agronomic Reasoning
        AgronomicPrescription prescription = reasoningService.generatePrescription(
                visionResult,
                telemetry,
                request.farmProfile()
        );

        return ResponseEntity.ok(new DiagnosticResponse(visionResult, prescription));
    }

    @PostMapping("/vision")
    public ResponseEntity<VisionAnalysisResult> runVisionOnly(
            @RequestBody DiagnosticRequest request,
            @RequestHeader(value = "X-Api-Key", required = false) String clientKey
    ) {
        VisionAnalysisResult result = visionService.analyzeLeaf(
                request.imageBase64(),
                request.cropHint(),
                clientKey
        );
        return ResponseEntity.ok(result);
    }
}

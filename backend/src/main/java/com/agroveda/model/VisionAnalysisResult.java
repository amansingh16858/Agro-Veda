package com.agroveda.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VisionAnalysisResult(
    boolean isPlant,
    String rejectionReason,
    String cropIdentified,
    String healthStatus,
    List<String> visualSymptoms,
    List<String> suspectedDeficiencies,
    String suspectedPathology,
    String simplePathologyMeaning,
    double confidenceScore,
    String severityLevel,
    List<VisualSymptomBox> symptomBoxes,
    long executionTimeMs,
    Integer tokensUsed,
    String modelUsed,
    Boolean isFallback,
    String errorMessage
) {}

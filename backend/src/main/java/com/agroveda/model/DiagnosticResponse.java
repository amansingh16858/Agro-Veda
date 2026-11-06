package com.agroveda.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DiagnosticResponse(
    VisionAnalysisResult vision,
    AgronomicPrescription prescription
) {}

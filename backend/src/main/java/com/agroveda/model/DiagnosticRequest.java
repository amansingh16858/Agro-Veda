package com.agroveda.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DiagnosticRequest(
    String imageBase64,
    FarmProfile farmProfile,
    SoilTelemetry telemetry,
    String cropHint
) {}

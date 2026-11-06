package com.agroveda.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SoilTelemetry(
    double moisturePercent,
    double temperatureC,
    double humidityPercent,
    Double soilPH,
    String timestamp,
    Boolean isSimulated,
    Boolean isDhtLive
) {}

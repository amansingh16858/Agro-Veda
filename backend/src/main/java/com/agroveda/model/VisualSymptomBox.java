package com.agroveda.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VisualSymptomBox(
    String label,
    String simpleMeaning,
    double confidence,
    BoxArea area
) {
    public record BoxArea(double x, double y, double width, double height) {}
}

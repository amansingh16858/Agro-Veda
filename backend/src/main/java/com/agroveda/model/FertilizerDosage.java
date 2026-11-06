package com.agroveda.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FertilizerDosage(
    String name,
    String type,
    String ratioNPK,
    String exactDosage,
    String applicationMethod,
    String timing,
    int estimatedCostINR,
    String safetyWarning
) {}

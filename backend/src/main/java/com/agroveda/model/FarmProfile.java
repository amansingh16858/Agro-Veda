package com.agroveda.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FarmProfile(
    String cropName,
    String soilType,
    String plotArea,
    String region
) {}

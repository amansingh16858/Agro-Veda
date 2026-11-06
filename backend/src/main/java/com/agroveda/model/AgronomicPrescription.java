package com.agroveda.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AgronomicPrescription(
    String primaryDiagnosis,
    SimpleSummary simpleSummary,
    String multimodalCorrelation,
    String rootCauseAnalysis,
    List<FertilizerDosage> chemicalPrescription,
    List<FertilizerDosage> organicPrescription,
    IrrigationDirective irrigationDirective,
    List<ScheduleTask> fourteenDaySchedule,
    EstimatedCost totalEstimatedCostINR,
    AcademicNotes academicNotes,
    Boolean isFallback,
    SoilTelemetry snapshotTelemetry
) {
    public record SimpleSummary(String whatHappened, String whatToDoToday) {}
    public record IrrigationDirective(String action, String details, double targetMoisturePercent) {}
    public record ScheduleTask(String day, String task, String type) {}
    public record EstimatedCost(int chemical, int organic) {}
    public record AcademicNotes(
        int stage1Tokens,
        int stage2Tokens,
        double totalCostUSD,
        long totalLatencyMs,
        String reasoningChainSummary
    ) {}
}

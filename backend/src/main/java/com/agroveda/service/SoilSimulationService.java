package com.agroveda.service;

import com.agroveda.model.SoilTelemetry;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SoilSimulationService {

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
    
    // Default simulated soil telemetry
    private final AtomicReference<SoilTelemetry> currentTelemetry = new AtomicReference<>(
        new SoilTelemetry(
            48.0,
            26.5,
            62.0,
            6.8,
            LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a")),
            true,
            false
        )
    );

    private double manualMoisture = 48.0;
    private double manualTemp = 26.5;
    private double manualHumidity = 62.0;
    private double manualPH = 6.8;
    private boolean isManuallyOverridden = false;

    public SoilTelemetry getCurrentTelemetry() {
        String timestamp = LocalTime.now().format(timeFormatter);

        if (isManuallyOverridden) {
            return new SoilTelemetry(
                manualMoisture,
                manualTemp,
                manualHumidity,
                manualPH,
                timestamp,
                true,
                false
            );
        }

        // Generate gentle, natural environmental micro-fluctuations (+/- 0.2%)
        double variation = (Math.sin(System.currentTimeMillis() / 8000.0) * 0.4);
        double moisture = Math.round((manualMoisture + variation) * 10.0) / 10.0;
        double temp = Math.round((manualTemp + (variation * 0.15)) * 10.0) / 10.0;
        double humidity = Math.round((manualHumidity + (variation * 0.2)) * 10.0) / 10.0;

        SoilTelemetry updated = new SoilTelemetry(
            moisture,
            temp,
            humidity,
            manualPH,
            timestamp,
            true,
            false
        );

        currentTelemetry.set(updated);
        return updated;
    }

    public void setManualOverride(double moisture, Double temp, Double humidity, Double ph) {
        this.manualMoisture = Math.max(0, Math.min(100, moisture));
        if (temp != null) this.manualTemp = temp;
        if (humidity != null) this.manualHumidity = humidity;
        if (ph != null) this.manualPH = ph;
        this.isManuallyOverridden = true;
    }

    public void resetToAutomaticSimulation() {
        this.isManuallyOverridden = false;
    }
}

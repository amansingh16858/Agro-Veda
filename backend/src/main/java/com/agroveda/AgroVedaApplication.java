package com.agroveda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgroVedaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgroVedaApplication.class, args);
        System.out.println("=================================================");
        System.out.println("🌾 AgroVeda AI Java Backend Running on port 8080");
        System.out.println("🌾 Telemetry Endpoint:  http://localhost:8080/api/telemetry");
        System.out.println("🌾 Diagnostic Endpoint: http://localhost:8080/api/diagnose");
        System.out.println("🌾 Health Endpoint:     http://localhost:8080/api/health");
        System.out.println("=================================================");
    }
}

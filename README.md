# 🌾 AgroVeda AI — Smart Fertilizer & Crop Diagnostic System
### *Modern Java 21 Backend + React 19 Precision Agronomy Engine*

> **Precision Agriculture Dual-Engine**: Fusing agronomic soil telemetry with high-resolution computer vision to prescribe exact chemical (NPK) and organic fertilizer remedies.

---

## 🌟 Architecture: Pure Java 21 Backend + Modern React Frontend

- **Backend**: **100% Java 21 (Spring Boot 3.3.5)** running on `http://localhost:8080`
  - `SoilSimulationService.java`: Realistic agronomic soil dynamics (moisture, temperature, humidity, pH) with preset and manual slider controls.
  - `CropVisionService.java`: Multimodal Vision analysis engine with Gemini / Grok API support and clinical fallback logic.
  - `AgronomicReasoningService.java`: Multimodal correlation calculus prescribing calibrated NPK ratios, commercial fertilizers, and organic treatments.
- **Frontend**: **React 19, TypeScript, and Tailwind CSS** running on `http://localhost:5173`
  - Interactive Leaf Diagnostic Studio with low-light optical boost (+22% brightness).
  - Telemetry HUD with smooth Apple-style gauges and Java soil engine controls.
  - 14-day agricultural care schedule and downloadable prescription reports.
- **Hardware Decoupled**: Physical COM/serial and C++ firmware dependencies have been completely replaced with a pure software simulation engine. Legacy hardware sketches are preserved in `legacy_hardware_backup/`.

---

## 🚀 Quickstart

### 1. Launch All Services (One-Click Windows)
Double-click `run.bat` or execute in terminal:
```bash
.\run.bat
```

### 2. Manual Commands
**Backend (Java 21):**
```bash
cd backend
mvnw.cmd clean package -DskipTests
java -jar target/agroveda-backend-1.0.0.jar
```
*Health check:* `http://localhost:8080/api/health`

**Frontend (React 19):**
```bash
npm install
npm run dev
```
*Live UI:* `http://localhost:5173/`

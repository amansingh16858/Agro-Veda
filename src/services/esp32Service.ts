import { SoilTelemetry } from '../types';

let lastDeliveredMoisture = 48.0;

export async function fetchESP32Telemetry(deviceIp?: string): Promise<SoilTelemetry> {
  const targets = deviceIp && deviceIp.trim() !== ''
    ? [deviceIp.trim(), '/telemetry', '/api/telemetry']
    : ['/api/telemetry', '/telemetry', 'http://127.0.0.1:8080/api/telemetry'];

  for (const target of targets) {
    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 2000);
      const url = target.startsWith('http') || target.startsWith('/')
        ? target 
        : `http://${target}/api/telemetry`;
      const res = await fetch(url, { signal: controller.signal });
      clearTimeout(timeoutId);

      if (res.ok) {
        const json = await res.json();
        const rawM = json.moisturePercent ?? json.soil_moisture;
        if (rawM !== undefined && rawM !== null) {
          const val = Number(rawM);
          if (Math.abs(val - lastDeliveredMoisture) >= 0.5) {
            lastDeliveredMoisture = Math.round(val * 10) / 10;
          }

          return {
            moisturePercent: lastDeliveredMoisture,
            temperatureC: Number((json.temperatureC ?? json.temperature ?? 26.5).toFixed(1)),
            humidityPercent: Number((json.humidityPercent ?? json.humidity ?? 62.0).toFixed(1)),
            soilPH: json.soilPH ? Number(json.soilPH.toFixed(1)) : 6.8,
            timestamp: json.timestamp || new Date().toLocaleTimeString(),
            isSimulated: json.isSimulated !== false,
            isDhtLive: Boolean(json.isDhtLive)
          };
        }
      }
    } catch {
      // Try next target
    }
  }

  // Graceful baseline if backend is briefly restarting
  return {
    moisturePercent: 48.0,
    temperatureC: 26.5,
    humidityPercent: 62.0,
    soilPH: 6.8,
    timestamp: new Date().toLocaleTimeString(),
    isSimulated: true
  };
}

export async function updateSoilMoistureOverride(moisture: number, soilPH?: number): Promise<void> {
  try {
    await fetch('/api/telemetry/override', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ moisture, soilPH: soilPH ?? 6.8 })
    });
  } catch {
    // Non-blocking
  }
}

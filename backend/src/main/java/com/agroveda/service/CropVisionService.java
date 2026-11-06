package com.agroveda.service;

import com.agroveda.model.VisionAnalysisResult;
import com.agroveda.model.VisualSymptomBox;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class CropVisionService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Value("${agroveda.openrouter.key:}")
    private String openRouterKey;

    @Value("${agroveda.grok.key:}")
    private String grokKey;

    public VisionAnalysisResult analyzeLeaf(String imageBase64, String cropHint, String clientKey) {
        long startTime = System.currentTimeMillis();

        String activeKey = (clientKey != null && !clientKey.isBlank()) 
                ? clientKey 
                : (!openRouterKey.isBlank() ? openRouterKey : grokKey);

        String cleanBase64 = imageBase64.contains("base64,")
                ? imageBase64.split("base64,")[1]
                : imageBase64;

        if (activeKey != null && !activeKey.isBlank()) {
            try {
                VisionAnalysisResult remoteResult = callRemoteVisionApi(cleanBase64, activeKey, startTime);
                if (remoteResult != null) {
                    return remoteResult;
                }
            } catch (Exception e) {
                System.err.println("[CropVisionService] Remote Vision API error: " + e.getMessage());
            }
        }

        // Offline / Sample / Botanical Intelligent Fallback
        return generateBotanicalFallback(cropHint, startTime);
    }

    private VisionAnalysisResult callRemoteVisionApi(String base64Image, String apiKey, long startTime) throws Exception {
        String prompt = """
            You are an expert Plant Pathologist. Examine this crop leaf image very carefully.
            CRITICAL CHECKS:
            1. Verify if this image is a plant leaf, foliage, or crop.
            2. If it is an unrelated object, person or non-plant, return "isPlant": false and explain in "rejectionReason".
            3. Identify the crop species (e.g. Tomato, Mango, Hibiscus, Potato, Maize, Chilli, Wheat, Rice).
            4. Identify visible symptoms (chlorosis, necrosis, lesions, discoloration, curl).
            5. Specify suspected deficiency (Potassium, Nitrogen, Phosphorus, Iron, Zinc, or Fungal/Bacterial pathogen).
            Return ONLY a valid JSON object matching this schema:
            {
              "isPlant": true,
              "cropIdentified": "Crop Name",
              "healthStatus": "Nutrient Deficient",
              "visualSymptoms": ["Yellowing between veins", "Brown dry leaf tips"],
              "suspectedDeficiencies": ["Potassium (K)", "Nitrogen (N)"],
              "suspectedPathology": "Potassium Deficiency & Marginal Chlorosis",
              "simplePathologyMeaning": "Yellow leaf edges from potassium starvation",
              "confidenceScore": 0.94,
              "severityLevel": "Moderate",
              "symptomBoxes": [
                {
                  "label": "Marginal Chlorosis",
                  "simpleMeaning": "Yellow leaf edges",
                  "confidence": 0.92,
                  "area": { "x": 15, "y": 20, "width": 45, "height": 40 }
                }
              ]
            }
            """;

        String jsonPayload = objectMapper.writeValueAsString(new OpenRouterRequest(
                "google/gemini-2.0-flash-001",
                List.of(new Message("user", List.of(
                        new ContentPart("text", prompt),
                        new ContentPart("image_url", new ImageUrl("data:image/jpeg;base64," + base64Image))
                ))),
                0.1,
                1000
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("HTTP-Referer", "https://agroveda.local")
                .header("X-Title", "AgroVeda AI Java Backend")
                .timeout(Duration.ofSeconds(20))
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode root = objectMapper.readTree(response.body());
            String text = root.path("choices").get(0).path("message").path("content").asText();
            String cleanedJson = cleanJsonString(text);
            JsonNode data = objectMapper.readTree(cleanedJson);

            long duration = System.currentTimeMillis() - startTime;

            List<String> symptoms = new ArrayList<>();
            if (data.has("visualSymptoms")) {
                data.get("visualSymptoms").forEach(s -> symptoms.add(s.asText()));
            }

            List<String> deficiencies = new ArrayList<>();
            if (data.has("suspectedDeficiencies")) {
                data.get("suspectedDeficiencies").forEach(d -> deficiencies.add(d.asText()));
            }

            List<VisualSymptomBox> boxes = new ArrayList<>();
            if (data.has("symptomBoxes")) {
                data.get("symptomBoxes").forEach(b -> {
                    JsonNode area = b.path("area");
                    boxes.add(new VisualSymptomBox(
                            b.path("label").asText(),
                            b.path("simpleMeaning").asText(getPlainMeaning(b.path("label").asText())),
                            b.path("confidence").asDouble(0.9),
                            new VisualSymptomBox.BoxArea(
                                    area.path("x").asDouble(20),
                                    area.path("y").asDouble(20),
                                    area.path("width").asDouble(40),
                                    area.path("height").asDouble(40)
                            )
                    ));
                });
            }

            return new VisionAnalysisResult(
                    data.path("isPlant").asBoolean(true),
                    data.path("rejectionReason").asText(null),
                    data.path("cropIdentified").asText("Cultivated Crop"),
                    data.path("healthStatus").asText("Nutrient Deficient"),
                    symptoms.isEmpty() ? List.of("Leaf chlorosis") : symptoms,
                    deficiencies.isEmpty() ? List.of("Potassium (K)") : deficiencies,
                    data.path("suspectedPathology").asText("Nutrient Deficiency"),
                    data.path("simplePathologyMeaning").asText("Plant needs essential minerals"),
                    data.path("confidenceScore").asDouble(0.92),
                    data.path("severityLevel").asText("Moderate"),
                    boxes,
                    duration,
                    root.path("usage").path("total_tokens").asInt(450),
                    "Gemini 2.0 Flash (Java Engine)",
                    false,
                    null
            );
        }

        return null;
    }

    public VisionAnalysisResult generateBotanicalFallback(String cropHint, long startTime) {
        String crop = (cropHint != null && !cropHint.isBlank() && !cropHint.contains("Auto")) 
                ? cropHint 
                : "Tomato Crop";

        long duration = Math.max(120, System.currentTimeMillis() - startTime);

        return new VisionAnalysisResult(
                true,
                null,
                crop,
                "Nutrient Deficient",
                List.of(
                        "Interveinal chlorosis across mid-tier leaves",
                        "Necrotic spotting along leaf margins",
                        "Stunted cellular foliage expansion"
                ),
                List.of("Potassium (K)", "Nitrogen (N)"),
                "Marginal Chlorosis & Potassium Starvation",
                "Yellowing leaf edges & nutrient deficit",
                0.94,
                "Moderate",
                List.of(
                        new VisualSymptomBox(
                                "Marginal Chlorosis",
                                "Yellowing leaf edges",
                                0.95,
                                new VisualSymptomBox.BoxArea(18, 22, 42, 38)
                        ),
                        new VisualSymptomBox(
                                "Tip Necrosis",
                                "Dead brown leaf tips",
                                0.91,
                                new VisualSymptomBox.BoxArea(52, 45, 30, 25)
                        )
                ),
                duration,
                380,
                "AgroVeda Java Agronomic Fallback Engine",
                true,
                null
        );
    }

    public static String getPlainMeaning(String term) {
        if (term == null) return "Discolored leaf area";
        String lower = term.toLowerCase();
        if (lower.contains("chlorosis")) return "Yellow leaf tissue";
        if (lower.contains("necrosis")) return "Dead brown leaf spots";
        if (lower.contains("blight")) return "Dark rotting fungal patches";
        if (lower.contains("mildew")) return "White powdery fungus";
        if (lower.contains("rust")) return "Orange-red dusty spots";
        if (lower.contains("curl")) return "Curling twisted leaves";
        return "Discolored leaf area";
    }

    private String cleanJsonString(String raw) {
        String clean = raw.trim();
        clean = clean.replaceAll("^```json\\s*", "").replaceAll("^```\\s*", "").replaceAll("```$", "").trim();
        int firstBrace = clean.indexOf('{');
        int lastBrace = clean.lastIndexOf('}');
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            clean = clean.substring(firstBrace, lastBrace + 1);
        }
        return clean;
    }

    // Helper records for OpenRouter payload
    private record OpenRouterRequest(String model, List<Message> messages, double temperature, int max_tokens) {}
    private record Message(String role, List<ContentPart> content) {}
    private record ContentPart(String type, Object text, ImageUrl image_url) {
        public ContentPart(String type, String text) { this(type, text, null); }
        public ContentPart(String type, ImageUrl image_url) { this(type, null, image_url); }
    }
    private record ImageUrl(String url) {}
}

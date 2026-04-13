package org.unamur.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unamur.api.SonarApiClient;
import org.unamur.service.SonarService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class SonarServiceImpl implements SonarService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final SonarApiClient sonarApiClient;

    public Map<String, String> getSonarMetrics() {
        String responseBody = sonarApiClient.fetchMetrics();
        return parseSonarResponse(responseBody);
    }

    // Helper method for parsing the JSON
    private Map<String, String> parseSonarResponse(String jsonResponse) {
        Map<String, String> parsedMetrics = new HashMap<>();
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode measures = root.path("component").path("measures");

            if (measures.isArray()) {
                for (JsonNode measure : measures) {
                    parsedMetrics.put(measure.get("metric").asText(), measure.get("value").asText());
                }
            }
        } catch (Exception e) {
            log.error("Error parsing JSON from SonarCloud", e);
        }
        return parsedMetrics;
    }
}

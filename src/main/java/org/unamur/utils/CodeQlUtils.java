package org.unamur.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.unamur.dto.PrMetrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CodeQlUtils {
    private final ObjectMapper mapper = new ObjectMapper();

    public void runCodeQlAnalysis(File workingDir, String[] command) {
        try {
            System.out.println("Starting CodeQL analysis in: " + workingDir.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(workingDir);

            // Redirecting error stream to standard stream to capture everything in one reader
            pb.redirectErrorStream(true);

            Process process = pb.start();

            // CodeQL outputs a lot of progress data. It is important to read it
            // so the process doesn't hang and to provide feedback in logs.
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // You could log this to a file or a dashboard console
                    System.out.println("[CodeQL] " + line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("CodeQL analysis failed with exit code: " + exitCode);
            }

            System.out.println("Analysis completed successfully. Output: results.sarif");

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("CodeQL execution interrupted", e);
        }
    }

    public PrMetrics extract(File sarifFile, List<String> prFiles) throws Exception {
        JsonNode root = mapper.readTree(sarifFile);
        JsonNode results = root.at("/runs/0/results");

        Map<String, Integer> alertsByFile = new HashMap<>();
        int criticalCount = 0;

        for (JsonNode result : results) {
            String level = result.path("level").asText("warning");
            String filePath = result.at("/locations/0/physicalLocation/artifactLocation/uri").asText();

            // We only count metrics for files actually touched by the PR
            if (prFiles.contains(filePath)) {
                if ("error".equals(level)) criticalCount++;

                alertsByFile.put(filePath, alertsByFile.getOrDefault(filePath, 0) + 1);
            }
        }

        PrMetrics metrics = new PrMetrics(
                criticalCount,
                prFiles.size(),
                calculateRisk(criticalCount, prFiles.size()),
                List.of()
        );
//        metrics.criticalAlerts(criticalCount);
//        metrics.setTotalImpactedFiles(prFiles.size());
//        metrics.setRiskScore(calculateRisk(criticalCount, prFiles.size()));

        return metrics;
    }

    private double calculateRisk(int critical, int files) {
        // Simple formula: Weight critical bugs against the volume of change
        return (critical * 5.0) + (files * 0.5);
    }
}

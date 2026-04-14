package org.unamur.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.unamur.model.PullRequestMetrics;
import org.unamur.persistence.PrScanResult;
import org.unamur.repository.PrScanResultRepository;
import org.unamur.service.MetricsService;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service
public class MetricsServiceImpl implements MetricsService {

    private final PrScanResultRepository prScanResultRepository;

    @Override
    public PullRequestMetrics getMetrics(URI projectUrl, String prId) {

        var results = prScanResultRepository.findByPrIdAndProjectUrl(prId, projectUrl.toString()).get();

        var filteredResults = new PullRequestMetrics();

        // TODO return all metrics.
        return null;
    }

    @Override
    public void createOrUpdateMetrics(String prId, String projectUrl, MultipartFile sarifFile, MultipartFile impactedFiles) {

        try {
            String rawSarifJson = new String(sarifFile.getBytes(), StandardCharsets.UTF_8);
            String rawCsv = new String(impactedFiles.getBytes(), StandardCharsets.UTF_8);

            PrScanResult existingScanResult = prScanResultRepository.findByPrIdAndProjectUrl(prId, projectUrl)
                    .orElse(
                            PrScanResult.builder()
                                    .prId(prId)
                                    .projectUrl(projectUrl)
                                    .rawSarifJson(rawSarifJson)
                                    .impactedFilesCsv(rawCsv)
                                    .build()
                    );

            if(existingScanResult.getId() != null){
                existingScanResult.setRawSarifJson(rawSarifJson);
                existingScanResult.setImpactedFilesCsv(rawCsv);
            }

            prScanResultRepository.save(existingScanResult);

        } catch (IOException e) {
            log.error("Error processing metrics for project {} and PR {}: {}", projectUrl, prId, e.getMessage());
            return; // TODO
        }

        log.info("Processing metrics for project {} and PR {}", projectUrl, prId);
    }

    @Override
    public void createOrUpdateMetrics(String prId, String projectUrl, Map<String, String> sonarMetrics, String dotFile) {
        PrScanResult scanResult = prScanResultRepository.findByPrIdAndProjectUrl(prId, projectUrl).orElseThrow(() -> new IllegalArgumentException("PR scan result not found for project " + projectUrl + " and PR " + prId));
        scanResult.setCoverage(Float.valueOf(sonarMetrics.get("coverage")));
        scanResult.setBugs(Integer.valueOf(sonarMetrics.get("bugs")));
        scanResult.setCodeSmells(Integer.valueOf(sonarMetrics.get("code_smells")));
        scanResult.setVulnerabilities(Integer.valueOf(sonarMetrics.get("vulnerabilities")));
        scanResult.setSecurityHotspots(Integer.valueOf(sonarMetrics.get("security_hotspots")));
        scanResult.setDotFile(dotFile);
        prScanResultRepository.save(scanResult);
    }
}

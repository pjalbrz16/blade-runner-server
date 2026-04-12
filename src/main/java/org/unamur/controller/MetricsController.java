package org.unamur.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.unamur.api.MetricsApi;
import org.unamur.model.PullRequestMetrics;
import org.unamur.service.MetricsService;

import java.net.URI;

@AllArgsConstructor
@RestController
public class MetricsController implements MetricsApi {

    private final MetricsService metricsService;
    private final SimpMessagingTemplate template;

    @Override
    public ResponseEntity<PullRequestMetrics> getMetrics(URI projectUrl, String prId) {
        PullRequestMetrics metrics = metricsService.getMetrics(projectUrl, prId);
        return ResponseEntity.ok(metrics);
    }

    @Override
    public ResponseEntity<Void> postMetrics(String prId, String projectUrl, MultipartFile sarifFile, MultipartFile impactedFiles) {

        // TODO : save to H2
        metricsService.processMetrics(prId, projectUrl, sarifFile, impactedFiles);

        template.convertAndSend("/topic/metrics/%s".formatted(prId), "READY");

        return ResponseEntity.ok().build();
    }
}

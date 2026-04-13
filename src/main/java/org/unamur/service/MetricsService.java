package org.unamur.service;

import org.springframework.web.multipart.MultipartFile;
import org.unamur.model.PullRequestMetrics;

import java.net.URI;
import java.util.Map;

public interface MetricsService {
    PullRequestMetrics getMetrics(URI projectUrl, String prId);

    void createOrUpdateMetrics(String prId, String projectUrl, MultipartFile sarifFile, MultipartFile impactedFiles);

    void createOrUpdateMetrics(String prId, String projectUrl, Map<String, String> sonarMetrics);
}

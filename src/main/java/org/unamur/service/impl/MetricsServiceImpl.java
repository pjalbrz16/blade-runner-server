package org.unamur.service.impl;

import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.unamur.model.PullRequestMetrics;
import org.unamur.service.MetricsService;

@Slf4j
@AllArgsConstructor
@Service
public class MetricsServiceImpl implements MetricsService {

    @Override
    public PullRequestMetrics getMetrics(URI projectUrl, String prId) {
        return null;
    }

    @Override
    public void processMetrics(String prId, String projectUrl, MultipartFile sarifFile, MultipartFile impactedFiles) {
        log.info("Processing metrics for project {} and PR {}", projectUrl, prId);
    }
}

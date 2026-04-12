package org.unamur.service;

import java.net.URI;
import org.springframework.web.multipart.MultipartFile;
import org.unamur.model.PullRequestMetrics;

public interface MetricsService {

    PullRequestMetrics getMetrics(URI projectUrl, String prId);

    void processMetrics(String prId, String projectUrl, MultipartFile sarifFile, MultipartFile impactedFiles);

}

package org.unamur.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unamur.api.GithubApiClient;
import org.unamur.config.AppProperties;
import org.unamur.service.RemoteRepositoryService;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class RemoteRepositoryServiceImpl implements RemoteRepositoryService {

    private final AppProperties appProperties;

    private final GithubApiClient githubApiClient;

    @Override
    public List<Map<String, Object>> listPR(String repositoryUrl) {

        // Extract owner and repo from URL
        // Example: https://github.com/pjalbrz16/my-app.git -> ["pjalbrz16", "my-app"]
        String path = repositoryUrl.replace("https://github.com/", "").replace(".git", "");
        String[] parts = path.split("/");
        String owner = parts[0];
        String repo = parts[1];

        return githubApiClient.getOpenPrForProject(owner, repo);
    }

    @Override
    public void triggerScannerForPullRequest(URI projectUrl, String prId) {

        Map<String, Object> variables = Map.of(
                "ref", "main", // The branch where the workflow file exists
                "inputs", Map.of(
                        "prId", prId,
                        "projectUrl", projectUrl,
                        "backendUrl", appProperties.getUrl()
                )
        );

        githubApiClient.triggerScannerForPullRequest(variables);

        log.info("Triggered scanner for project {} with PR {}", projectUrl, prId);
    }

}

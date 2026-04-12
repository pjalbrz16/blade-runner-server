package org.unamur.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.unamur.config.AppProperties;
import org.unamur.config.TokenProperties;
import org.unamur.enums.RepositoryType;
import org.unamur.service.RemoteRepositoryService;
import org.unamur.service.WebRepositoryStrategy;
import org.unamur.utils.GitUtils;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class RemoteRepositoryServiceImpl implements RemoteRepositoryService {

    private final GitUtils gitUtils;

    private final static String language = "java";

    private final RestClient githubRestClient;

    private final TokenProperties tokenProperties;

    private final AppProperties appProperties;

    private final Map<RepositoryType, WebRepositoryStrategy> strategies = Map.of(
            RepositoryType.GITHUB, new GithubRepositoryStrategy(),
            RepositoryType.AZURE, new AzureRepositoryStrategy(),
            RepositoryType.GITLAB, new GitlabRepositoryStrategy()
    );
    private final RestClient restClient;

    @Override
    public List<Map<String, Object>> listPR(String repositoryUrl) {

        // Extract owner and repo from URL
        // Example: https://github.com/pjalbrz16/my-app.git -> ["pjalbrz16", "my-app"]
        String path = repositoryUrl.replace("https://github.com/", "").replace(".git", "");
        String[] parts = path.split("/");
        String owner = parts[0];
        String repo = parts[1];

        return  githubRestClient.get()
                .uri("/repos/{owner}/{repo}/pulls?state=open", owner, repo)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProperties.getGithubPat())
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
    }


    @Override
    public void triggerScannerForPullRequest(URI projectUrl, String prId) {
        String workflowId = "scanner.yaml";

        Map<String, Object> variables = Map.of(
                "ref", "main", // The branch where the workflow file exists
                "inputs", Map.of(
                        "prId", prId,
                        "projectUrl", projectUrl,
                        "backendUrl", appProperties.getUrl()
                )
        );

        restClient.post()
                .uri("{uri}/actions/workflows/{workflowId}/dispatches", appProperties.getWorkerUrl(), workflowId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProperties.getGithubPat())
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .body(variables)
                .retrieve()
                .toBodilessEntity();

        log.info("Triggered scanner for project {} with PR {}", projectUrl, prId);
    }

}

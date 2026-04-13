package org.unamur.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.unamur.config.AppProperties;
import org.unamur.config.TokenProperties;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GithubApiClient {

    private final RestClient client;

    private final AppProperties appProperties;

    private final static String WORKFLOW_ID = "scanner.yaml";

    public GithubApiClient(TokenProperties tokenProperties, AppProperties appProperties) {
        this.appProperties = appProperties;
        this.client = RestClient.builder()
                .baseUrl(appProperties.getWorkerUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProperties.getGithubPat())
                .build();
    }

    public List<Map<String, Object>> getOpenPrForProject(String owner, String repository) {
        return client.get()
                .uri("/repos/{owner}/{repo}/pulls?state=open", owner, repository)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public void triggerScannerForPullRequest(Map<String, Object> variables) {
        log.info("Triggering %s/actions/workflows/%s/dispatches".formatted(appProperties.getWorkerUrl(), WORKFLOW_ID));

        log.info("Variables: {}", variables);

        client.post()
                .uri("/actions/workflows/{workflowId}/dispatches", WORKFLOW_ID)
                .header(org.springframework.http.HttpHeaders.ACCEPT, "application/vnd.github+json")
                .body(variables)
                .retrieve()
                .toBodilessEntity();
    }

}

package org.unamur.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.unamur.api.GithubApi;
import org.unamur.model.GithubRepo;

import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
public class GithubApiController implements GithubApi {

    private final WebClient webClient;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public ResponseEntity<Map<String, Object>> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            return ResponseEntity.ok(oauth2User.getAttributes());
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<List<GithubRepo>> getMyRepos() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                "github",
                authentication.getName()
        );

        String githubToken = client.getAccessToken().getTokenValue();

        List<GithubRepo> repos = webClient.get()
                .uri("/user/repos")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .retrieve()
                .bodyToFlux(GithubRepo.class)
                .collectList()
                .block();

        return ResponseEntity.ok(repos);
    }
}

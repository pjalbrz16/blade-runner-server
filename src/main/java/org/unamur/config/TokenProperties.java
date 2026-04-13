package org.unamur.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "token")
@Data
public class TokenProperties {
    private String githubPat;
    private String backendToken;
    private String frontendToken;
    private String sonarToken;
}

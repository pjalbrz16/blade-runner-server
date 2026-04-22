package org.unamur.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
    private String url;
    private String githubWorkerRepo;
    private String githubOwner;
    private String githubUrl;
    private String sonarComponentKey;
}

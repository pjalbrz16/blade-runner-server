package org.unamur.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "config")
public record GlobalProperties(
        String cloneDir,
        String databaseDir,
        String queryDir
){}

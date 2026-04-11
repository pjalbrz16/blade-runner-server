package org.unamur.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.unamur.properties.GlobalProperties;

@Configuration
public class GlobalConfig {

    @Bean
    public GlobalProperties properties(@Value("${config.cloneDir}") String cloneDir,
                                       @Value("${config.databaseDir}") String databaseDir,
                                       @Value("${config.queryDir}") String queryDir){
        return new GlobalProperties(cloneDir, databaseDir, queryDir);
    }

}

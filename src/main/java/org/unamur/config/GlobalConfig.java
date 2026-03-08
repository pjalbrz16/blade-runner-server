package org.unamur.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.unamur.properties.GlobalProperties;

@Configuration
public class GlobalConfig {

    @Bean
    public GlobalProperties properties(@Value("${cloneDir}") String cloneDir,
                                       @Value("${databaseDir}") String databaseDir,
                                       @Value("${queryDir}") String queryDir){
        return new GlobalProperties(cloneDir, databaseDir, queryDir);
    }

}

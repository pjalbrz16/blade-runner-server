package org.unamur.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unamur.properties.GlobalProperties;
import org.unamur.service.WorkspaceService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@AllArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private final GlobalProperties globalProperties;

    @Override
    public File getLocalFolderForProject(String project){
        String folderName = project.replaceAll("[^a-zA-Z0-9]", "_");
        Path path = Paths.get(globalProperties.cloneDir());
        File projectDir = path.resolve(folderName).toFile();
        if(!projectDir.exists()){
            if(!projectDir.mkdirs()) {
                throw new RuntimeException("Unable to create directory for project %s".formatted(project));
            }
        }
        return projectDir;
    }
}

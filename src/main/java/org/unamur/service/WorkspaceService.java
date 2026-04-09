package org.unamur.service;

import java.io.File;

public interface WorkspaceService {
    File getLocalFolderForProject(String url);
}

package org.unamur.service;

import java.nio.file.Path;

public interface CodeQLService {

    void createDatabase(Path repositoryPath);

    void executeQueries();

}

package org.unamur.service;

import java.net.URI;
import java.nio.file.Path;

public interface CodeQLService {

    void createDatabase(Path repositoryPath);

    void executeQueries();

    void selectAndPrepareDatabase(URI project, String selectedPr);

}

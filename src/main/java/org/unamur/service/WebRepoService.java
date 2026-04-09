package org.unamur.service;

import java.net.URI;
import java.util.List;

public interface WebRepoService {

    void processRepository(URI repositoryUrl);

    void fetchRepository(URI repositoryUrl);

    List<String> listPR(String repositoryUrl);

    void selectPR(String selectedPr);

}

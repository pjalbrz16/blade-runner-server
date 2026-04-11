package org.unamur.service;

import org.unamur.dto.PrMetadata;

import java.net.URI;
import java.util.List;

public interface WebRepoService {

    void processRepository(URI repositoryUrl);

    void fetchRepository(URI repositoryUrl);

    List<String> listPR(String repositoryUrl);

    PrMetadata selectAndPreparePR(URI project, String selectedPr);

}

package org.unamur.service;

import java.net.URI;

public interface WebRepoService {

    void processRepository(URI repositoryUrl);

    void fetchRepository(URI repositoryUrl);

}

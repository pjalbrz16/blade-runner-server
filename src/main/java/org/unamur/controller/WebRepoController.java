package org.unamur.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.unamur.api.WebRepoApi;
import org.unamur.model.SelectPrPost200Response;
import org.unamur.model.SelectPrPostRequest;
import org.unamur.service.CodeQLService;
import org.unamur.service.WebRepoService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
public class WebRepoController implements WebRepoApi {

    private final WebRepoService webRepoService;

    private final CodeQLService codeQLService;


    @Override
    public ResponseEntity<List<String>> listPrGet(URI projectUrl) {
        return ResponseEntity.of(Optional.of(webRepoService.listPR(projectUrl.toString())));
    }

    @Override
    public ResponseEntity<SelectPrPost200Response> selectPrPost(SelectPrPostRequest selectPrPostRequest) {
        codeQLService.selectAndPrepareDatabase(selectPrPostRequest.getProjectUrl(), selectPrPostRequest.getPrId()); // TODO Move to another controller
        return WebRepoApi.super.selectPrPost(selectPrPostRequest);
    }
}

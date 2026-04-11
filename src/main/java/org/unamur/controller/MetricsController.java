package org.unamur.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.unamur.api.JobsApi;
import org.unamur.model.CreateJobRequest;
import org.unamur.model.CreateJobResponse;
import org.unamur.model.ExecutedJobsResponse;
import org.unamur.model.FinalJobStatus;
import org.unamur.service.WebRepoService;

@AllArgsConstructor
@RestController
public class MetricsController implements JobsApi {

    private final WebRepoService webRepoService;

    @Override
    public ResponseEntity<CreateJobResponse> createJob(CreateJobRequest createJobRequest) {
        webRepoService.processRepository(createJobRequest.getRepoUrl());

        return JobsApi.super.createJob(createJobRequest);
    }

    @Override
    public ResponseEntity<ExecutedJobsResponse> listExecutedJobs(FinalJobStatus status, Integer limit, Integer offset) {
        return JobsApi.super.listExecutedJobs(status, limit, offset);
    }
}

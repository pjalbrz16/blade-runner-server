package org.unamur.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.unamur.enums.RepositoryType;
import org.unamur.properties.GlobalProperties;
import org.unamur.service.WebRepoService;
import org.unamur.service.WebRepositoryStrategy;
import org.unamur.service.WorkspaceService;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

@Slf4j
@Service
@AllArgsConstructor
public class WebRepoServiceImpl implements WebRepoService {

    private final GlobalProperties globalProperties;

    private final WorkspaceService workspaceService;

    private final static String language = "java";

    private final Map<RepositoryType, WebRepositoryStrategy> strategies = Map.of(
            RepositoryType.GITHUB, new GithubRepositoryStrategy(),
            RepositoryType.AZURE, new AzureRepositoryStrategy(),
            RepositoryType.GITLAB, new GitlabRepositoryStrategy()
    );

    public void fetchRepository(URI repositoryUrl){
        String scriptPath = "";

        ProcessBuilder processBuilder = new ProcessBuilder(
                "bash -lc %s %s"
        );
    }

    @Override
    public List<String> listPR(String repositoryUrl) {
        RepositoryType type = RepositoryType.fromUrl(repositoryUrl);
        WebRepositoryStrategy strategy = strategies.get(type);

        File workingDir = workspaceService.getLocalFolderForProject(repositoryUrl);

        ensureRepoExists(workingDir, repositoryUrl);

        for(String command : strategy.getListPrCommand()){
            executeCommand(command, workingDir, strategy);
        }

        return strategy.getPrList();
    }

    @Override
    public void selectPR(String selectedPr) {

    }

    @Async
    @Override
    public void processRepository(URI repositoryUrl) {
        log.info("Starting repo processing %s ".formatted(repositoryUrl.toString()));

        List<String> command = List.of(
                "wsl",
                "bash", "-lc",
                String.format(
                        "%s %s %s %s %s",
                        "/mnt/g/idea/spring-mem/src/main/resources/work/scripts.sh",
                        repositoryUrl,
                        globalProperties.cloneDir(),
                        language,
                        globalProperties.queryDir(),
                        globalProperties.databaseDir()
                ));

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(false);


        int exit;
        try (ExecutorService exec = Executors.newFixedThreadPool(2)) {
            Process p = pb.start();

            Future<?> outTask = exec.submit(() ->
                    streamToLogger(p.getInputStream(), false)
            );
            Future<?> errTask = exec.submit(() ->
                    streamToLogger(p.getErrorStream(), true)
            );

            // Optional: timeout
            boolean finished = p.waitFor(30, TimeUnit.MINUTES);
            if (!finished) {
                p.destroyForcibly();
                throw new RuntimeException("Script timed out");
            }

            exit = p.exitValue();

            // Ensure stream tasks complete
            outTask.get(10, TimeUnit.SECONDS);
            errTask.get(10, TimeUnit.SECONDS);

            exec.shutdownNow();
        } catch (IOException | TimeoutException | InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }

    }

    private void streamToLogger(InputStream is, boolean isErr) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (isErr) {
                    log.error("[script] {}", line);
                } else {
                    log.info("[script] {}", line);
                }
            }
        } catch (IOException e) {
            log.warn("Failed reading process stream", e);
        }
    }

    private void executeCommand(String command, File workingDir, WebRepositoryStrategy strategy) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
            pb.directory(workingDir);
            Process process = pb.start();
            strategy.processPrList(process.getInputStream());
            process.waitFor();
        } catch (Exception e) {
            log.error("Error while executing command to list pull requests %s".formatted(e.getMessage()));
        }
    }


    private void ensureRepoExists(File dir, String url) {
        // If folder is empty, run 'git clone'
        if (Objects.requireNonNull(dir.list()).length == 0) {
            executeCommand("git clone " + url + " .", dir, null);
        }
    }

}

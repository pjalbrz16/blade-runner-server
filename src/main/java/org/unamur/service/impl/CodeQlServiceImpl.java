package org.unamur.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.unamur.dto.PrMetadata;
import org.unamur.service.CodeQLService;
import org.unamur.service.WebRepoService;
import org.unamur.utils.CodeQlUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Path;

@Slf4j
@Service
@AllArgsConstructor
public class CodeQlServiceImpl implements CodeQLService {

    private final WebRepoService webRepoService;

    private final CodeQlUtils codeQlUtils;

    @Override
    public void createDatabase(Path repositoryPath) {

    }

    @Override
    public void executeQueries() {

    }

    public void createDatabase(File projectRoot, String projectName) {
        String dbPath = String.format("resources/codeQl/%s/my-db", projectName);
        String sourceRoot = String.format("resources/codeQl/%s", projectName);

        File dbDir = new File(projectRoot, dbPath);
        if (dbDir.exists()) {
            FileUtils.deleteQuietly(dbDir);
        }

        String[] command = {
                "codeql", "database", "create", dbPath,
                "--language=java",
                "--source-root=" + sourceRoot,
                "--command=mvn clean compile" // Adjust based on your build tool
        };

        executeCommand(projectRoot, command);
    }

    @Override
    public void selectAndPrepareDatabase(URI project, String selectedPr) {
        PrMetadata metadata = webRepoService.selectAndPreparePR(project, selectedPr);

        String includePaths = String.join(",", metadata.impactedFiles());

        String[] codeQlCreateDatabaseCommand = {
                "codeql", "database", "create", "my-db",
                "--language=java",
                "--source-root=.",
                "--command=mvn clean install -DskipTests"
        };

        String[] codeQlAnalyzeCommand = {
                "codeql", "database", "analyze", "my-db",
                "java-security-and-quality.qls", // TODO Add this!
                "--format=sarif-latest",
                "--output=results.sarif",
                "--include-paths=%s".formatted(includePaths)
        };



        codeQlUtils.runCodeQlAnalysis(new File("my-db"), codeQlAnalyzeCommand);

        try{
            codeQlUtils.extract(null, metadata.impactedFiles());
        }catch (Exception e){

        }
    }

    public void analyzeDatabase(File projectRoot, String projectName, String includePaths) {
        String dbPath = String.format("resources/codeQl/%s/my-db", projectName);
        String outputPath = String.format("resources/codeQl/%s/results.sarif", projectName);

        String[] command = {
                "codeql", "database", "analyze", dbPath,
                "java-security-and-quality.qls",
                "--format=sarif-latest",
                "--output=" + outputPath,
                "--include-paths=" + includePaths
        };

        executeCommand(projectRoot, command);
    }

    private void executeCommand(File workingDir, String... command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(workingDir);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[" + command[0] + "] " + line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Command failed with exit code " + exitCode +
                        " during execution of: " + String.join(" ", command));
            }

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Execution failed for command: " + command[0], e);
        }
    }

}

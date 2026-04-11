package org.unamur.utils;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GitUtils {

    /**
     * Executes a command and returns the full output as a String.
     * Useful for getting single values like a merge-base hash.
     */
    public String execute(File workingDir, String... command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(workingDir);
            Process process = pb.start();

            // Read output
            String output;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                output = reader.lines().collect(Collectors.joining("\n"));
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // Log stderr for debugging if the command fails
                String error = readErrorStream(process);
                throw new RuntimeException("Git command failed with exit code " + exitCode + ": " + error);
            }

            return output;

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error executing git command", e);
        }
    }

    /**
     * Executes a command and returns the output as a List of lines.
     * Ideal for 'git diff --name-only' or 'git for-each-ref'.
     */
    public List<String> executeForList(File workingDir, String... command) {
        List<String> lines = new ArrayList<>();
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(workingDir);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        lines.add(line);
                    }
                }
            }

            if (process.waitFor() != 0) {
                throw new RuntimeException("Git command failed: " + readErrorStream(process));
            }

            return lines;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private String readErrorStream(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

}

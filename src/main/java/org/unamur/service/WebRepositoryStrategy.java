package org.unamur.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Getter
public abstract class WebRepositoryStrategy {

    private final List<String> prList = new ArrayList<>();

    public abstract List<String> getListPrCommand();

    public abstract String getPrReference();

    protected abstract Pattern getRepoSpecificPattern();

    public List<String> processPrList(InputStream inputStream){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;

            Pattern pattern = getRepoSpecificPattern();

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String prNumber = matcher.group(1);
                    prList.add(prNumber);
                    log.info("Discovered PR: #%s".formatted(prNumber));
                }
            }
        } catch (Exception e) {
            log.error("Error parsing PR list: %s".formatted(e.getMessage()));
        }
        return null;
    }
}

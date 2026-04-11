package org.unamur.service.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.unamur.service.WebRepositoryStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Getter
@NoArgsConstructor
public class GithubRepositoryStrategy extends WebRepositoryStrategy {

    @Override
    protected Pattern getRepoSpecificPattern(){
        return  Pattern.compile(".*/pr/(\\d+)");
    }

    @Override
    public String getPrReference() {
        return "origin/pr/";
    }

    @Override
    public List<String> getListPrCommand() {
        return List.of(
                "git fetch origin +refs/pull/*/head:refs/remotes/origin/pr/*",
                "git for-each-ref --format='%(refname:short)' refs/remotes/origin/pr/*"
        );
    }
}

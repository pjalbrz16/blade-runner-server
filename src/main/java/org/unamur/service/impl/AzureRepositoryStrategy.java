package org.unamur.service.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.unamur.service.WebRepositoryStrategy;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Getter
@NoArgsConstructor
public class AzureRepositoryStrategy extends WebRepositoryStrategy {

    @Override
    protected Pattern getRepoSpecificPattern(){
        return Pattern.compile(".*/azpr/(\\d+)");
    }

    @Override
    public List<String> getListPrCommand() {
        return List.of(
                "git fetch origin +refs/pull/*/head:refs/remotes/origin/azpr/*",
                "git for-each-ref --format=%(refname:short) refs/remotes/origin/azpr/*"
        );
    }

}

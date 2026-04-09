package org.unamur.enums;

import lombok.Getter;

@Getter
public enum RepositoryType {
    GITHUB("github.com"),
    AZURE("dev.azure.com"),
    GITLAB("gitlab.com");

    private final String urlPattern;

    RepositoryType(String urlPattern){
        this.urlPattern = urlPattern;
    }

    public static RepositoryType fromUrl(String url) {
        for(RepositoryType type : values()){
            if(url.contains(type.urlPattern)){
                return type;
            }
        }
        throw new UnsupportedOperationException("Unknown repository, create enum + corresponding strategy class");
    }
}

package org.unamur.dto;

public record ImpactedClass(
        String className,
        int alertCount,
        boolean inNewFile
) {}

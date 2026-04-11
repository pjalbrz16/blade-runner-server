package org.unamur.service;

import org.unamur.dto.InteractionDto;

import java.io.FileNotFoundException;
import java.util.List;

public interface DotGeneratorService {

    void createDotFile(List<InteractionDto> interactions, String outputPath) throws FileNotFoundException;
}

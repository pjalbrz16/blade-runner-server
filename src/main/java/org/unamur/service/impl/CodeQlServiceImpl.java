package org.unamur.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unamur.service.CodeQLService;
import org.unamur.utils.CodeQlUtils;

import java.nio.file.Path;

@Slf4j
@Service
@AllArgsConstructor
public class CodeQlServiceImpl implements CodeQLService {


    private final CodeQlUtils codeQlUtils;

    @Override
    public Object processSarifFile(Path sarifFile) {
        return null;
    }
}

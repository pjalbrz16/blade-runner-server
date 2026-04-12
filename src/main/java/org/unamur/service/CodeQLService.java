package org.unamur.service;

import java.nio.file.Path;

public interface CodeQLService {

    Object processSarifFile(Path sarifFile);
}

package com.philips.research.collector.core.domain;

import com.philips.research.collector.core.BusinessException;
import com.philips.research.collector.core.ImportService;
import com.philips.research.collector.core.spdx.SpdxParser;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
public class ImportInteractor implements ImportService {
    private final ProjectStore store;

    public ImportInteractor(ProjectStore store) {
        this.store = store;
    }

    @Override
    public void importSpdx(UUID projectId, InputStream stream) {
        final var project = store.readProject(projectId)
                .orElseThrow(() -> new BusinessException("No project '" + projectId + "' found"));

        new SpdxParser(project).parse(stream);
    }
}

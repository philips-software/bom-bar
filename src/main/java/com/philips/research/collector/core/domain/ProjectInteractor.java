package com.philips.research.collector.core.domain;

import com.philips.research.collector.core.NotFoundException;
import com.philips.research.collector.core.ProjectService;
import com.philips.research.collector.core.spdx.SpdxParser;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectInteractor implements ProjectService {
    private final ProjectStore store;

    public ProjectInteractor(ProjectStore store) {
        this.store = store;
    }

    @Override
    public ProjectDto createProject(String title) {
        final var project = store.createProject();
        project.setTitle(title);
        return toDto(project);
    }

    @Override
    public void importSpdx(UUID projectId, InputStream stream) {
        final Project project = validProject(projectId);

        new SpdxParser(project).parse(stream);
    }

    @Override
    public List<PackageDto> packages(UUID projectId) {
        return null;
    }

    private Project validProject(UUID projectId) {
        return store.readProject(projectId)
                .orElseThrow(() -> new NotFoundException("project", projectId));
    }

    private ProjectDto toDto(Project project) {
        final var dto = new ProjectDto();

        dto.id = project.getId();
        dto.title = project.getTitle();

        return dto;
    }
}

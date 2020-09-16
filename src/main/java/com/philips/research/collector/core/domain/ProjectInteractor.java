package com.philips.research.collector.core.domain;

import com.philips.research.collector.core.ProjectService;
import org.springframework.stereotype.Service;

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
    public List<PackageDto> packages(UUID projectId) {
        return null;
    }

    private ProjectDto toDto(Project project) {
        final var dto = new ProjectDto();

        dto.id = project.getId();
        dto.title = project.getTitle();

        return dto;
    }
}

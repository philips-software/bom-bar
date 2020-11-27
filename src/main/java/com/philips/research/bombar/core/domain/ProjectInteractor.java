/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.core.domain;

import com.philips.research.bombar.core.NotFoundException;
import com.philips.research.bombar.core.ProjectService;
import com.philips.research.bombar.core.domain.licenses.LicenseChecker;
import com.philips.research.bombar.core.domain.licenses.LicenseViolation;
import com.philips.research.bombar.core.domain.licenses.Licenses;
import com.philips.research.bombar.core.spdx.SpdxParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.tlinkowski.annotation.basic.NullOr;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class ProjectInteractor implements ProjectService {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectInteractor.class);

    private final PersistentStore store;

    public ProjectInteractor(PersistentStore store) {
        this.store = store;
    }

    @Override
    public List<ProjectDto> projects() {
        final var projects = store.getProjects().stream()
                .map(DtoConverter::toBaseDto)
                .collect(Collectors.toList());
        LOG.info("List all {} projects", projects.size());
        return projects;
    }

    @Override
    public ProjectDto createProject(@NullOr String title) {
        final var project = store.createProject();
        project.setTitle((title != null) ? title : project.getId().toString());
        LOG.info("Created project {}: {}", project.getId(), title);
        return DtoConverter.toDto(project);
    }

    @Override
    public ProjectDto getProject(UUID projectId) {
        final var project = validProject(projectId);
        LOG.info("Read project {}: {}", project.getId(), project.getTitle());
        return DtoConverter.toDto(project);
    }

    @Override
    public ProjectDto updateProject(ProjectDto dto) {
        final var project = validProject(dto.id);
        if (dto.title != null) {
            project.setTitle(dto.title);
        }
        updateEnum(Project.Distribution.class, dto.distribution, project::setDistribution);
        updateEnum(Project.Phase.class, dto.phase, project::setPhase);
        LOG.info("Update project {}: {}", project.getId(), project.getTitle());
        return DtoConverter.toDto(project);
    }

    <T extends Enum<?>> void updateEnum(Class<T> clazz, @NullOr String value, Consumer<T> setter) {
        if (value == null) return;

        Class<Project.Distribution> x = Project.Distribution.class;
        final var update = Arrays.stream(clazz.getEnumConstants())
                .filter(v -> v.name().equals(value.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("'" + value + "' is not a valid " + clazz.getSimpleName().toLowerCase()));
        setter.accept(update);
    }

    @Override
    public void importSpdx(UUID projectId, InputStream stream) {
        final var project = validProject(projectId);
        new SpdxParser(project, store).parse(stream);
        checkLicenses(project);
        LOG.info("Imported {} dependencies into project {}: {}", project.getDependencies().size(), project.getId(), project.getTitle());
    }

    private List<LicenseViolation> checkLicenses(Project project) {
        return new LicenseChecker(Licenses.REGISTRY, project).violations();
    }

    @Override
    public List<DependencyDto> getDependencies(UUID projectId) {
        final var project = validProject(projectId);
        LOG.info("Read {} dependencies from project {}: {}", project.getDependencies().size(), project.getId(), project.getTitle());

        return project.getDependencies().stream()
                .map(DtoConverter::toBaseDto)
                .collect(Collectors.toList());
    }

    @Override
    public DependencyDto getDependency(UUID projectId, String dependencyId) {
        final var project = validProject(projectId);
        final var dependency = project.getDependency(dependencyId)
                .orElseThrow(() -> new NotFoundException("dependency", dependencyId));
        final var violations = new LicenseChecker(Licenses.REGISTRY, project).violations(dependency);
        LOG.info("Read dependency {} from project {}", dependency, project);
        return DtoConverter.toDto(dependency, violations);
    }

    private Project validProject(UUID projectId) {
        return store.readProject(projectId)
                .orElseThrow(() -> new NotFoundException("project", projectId));
    }
}

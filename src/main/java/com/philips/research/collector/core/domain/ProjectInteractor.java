/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.collector.core.domain;

import com.philips.research.collector.core.NotFoundException;
import com.philips.research.collector.core.ProjectService;
import com.philips.research.collector.core.domain.licenses.LicenseChecker;
import com.philips.research.collector.core.domain.licenses.Licenses;
import com.philips.research.collector.core.spdx.SpdxParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectInteractor implements ProjectService {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectInteractor.class);

    private final ProjectStore store;

    public ProjectInteractor(ProjectStore store) {
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
    public ProjectDto createProject(String title) {
        final var project = store.createProject();
        project.setTitle(title);
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
    public void importSpdx(UUID projectId, InputStream stream) {
        final var project = validProject(projectId);
        new SpdxParser(project, store).parse(stream);
        LOG.info("Imported {} dependencies into project {}: {}", project.getDependencies().size(), project.getId(), project.getTitle());

        //TODO Temp experiment
        new LicenseChecker(Licenses.REGISTRY, project).verify().forEach(System.out::println);
    }

    @Override
    public List<DependencyDto> getDependencies(UUID projectId) {
        final var project = validProject(projectId);
        LOG.info("Read {} dependencies from project {}: {}", project.getDependencies().size(), project.getId(), project.getTitle());

        return project.getDependencies().stream()
                .map(DtoConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DependencyDto getDependency(UUID projectId, URI reference) {
        final var project = validProject(projectId);
        final var purl = new Purl(reference);
        final var dependency = project.getDependencies().stream()
                .filter(dep -> dep.getPackage().isPresent())
                .filter(dep -> purl.getReference().equals(dep.getPackage().get().getReference())
                        && purl.getVersion().equals(dep.getVersion()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("dependency", reference));
        LOG.info("Read dependency '{}' from project {}", reference, projectId);
        return DtoConverter.toDto(dependency);
    }

    private Project validProject(UUID projectId) {
        return store.readProject(projectId)
                .orElseThrow(() -> new NotFoundException("project", projectId));
    }
}

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
import com.philips.research.bombar.core.PersistentStore;
import com.philips.research.bombar.core.ProjectService;
import com.philips.research.bombar.core.domain.licenses.LicenseChecker;
import com.philips.research.bombar.core.domain.licenses.LicenseViolation;
import com.philips.research.bombar.core.domain.licenses.Licenses;
import com.philips.research.bombar.core.spdx.SpdxParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.tlinkowski.annotation.basic.NullOr;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Transactional
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
        LOG.info("List all projects ({})", projects.size());
        return projects;
    }

    @Override
    public ProjectDto createProject(@NullOr String title) {
        final var project = store.createProject();
        if (title != null) {
            project.setTitle(title);
        }
        LOG.info("Created new project {}", project);
        return DtoConverter.toDto(project);
    }

    @Override
    public ProjectDto getProject(UUID projectId) {
        final var project = validProject(projectId);
        LOG.info("Read project {}", project);
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
        LOG.info("Update project {}", project);
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
        // NOTE: Necessary because of failing orphan removal in JPA (reason is unknown)
        store.deleteDependencies(project);
        new SpdxParser(project, store).parse(stream);
        checkLicenses(project);
        LOG.info("Imported {} dependencies into project {}", project.getDependencies().size(), project);
    }

    private List<LicenseViolation> checkLicenses(Project project) {
        return new LicenseChecker(Licenses.REGISTRY, project).violations();
    }

    @Override
    public List<DependencyDto> getDependencies(UUID projectId) {
        final var project = validProject(projectId);
        LOG.info("Read {} dependencies from project {}", project.getDependencies().size(), project);

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

    @Override
    public void exempt(UUID projectId, URI reference, @NullOr String rationale) {
        final var project = validProject(projectId);
        if (rationale != null) {
            project.exempt(reference, rationale);
            LOG.info("Exempted {} for project {}", reference, project);
        } else {
            project.unexempt(reference);
            LOG.info("Dropped exemption of {} for project {}", reference, project);
        }
    }

    @Override
    public List<ProjectDto> findPackageUse(URI packageReference) {
        final var projects = new HashMap<UUID, ProjectDto>();
        store.getPackageDefinition(packageReference)
                .ifPresent(pkg ->
                        store.findDependencies(pkg)
                                .forEach(dep -> mergeIntoProjectsMap(dep, projects)));
        return new ArrayList<>(projects.values());
    }

    private void mergeIntoProjectsMap(Dependency dep, Map<UUID, ProjectDto> projects) {
        final var project = store.getProjectFor(dep);
        final var dto = projects.computeIfAbsent(project.getId(), id -> {
            final var proj = DtoConverter.toBaseDto(project);
            proj.packages = new ArrayList<>();
            return proj;
        });
        assert dto.packages != null;
        dto.packages.add(DtoConverter.toBaseDto(dep));
    }

    private Project validProject(UUID projectId) {
        return store.getProject(projectId)
                .orElseThrow(() -> new NotFoundException("project", projectId));
    }
}

/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain;

import com.philips.research.collector.core.NotFoundException;
import com.philips.research.collector.core.ProjectService;
import com.philips.research.collector.core.domain.licenses.LicenseChecker;
import com.philips.research.collector.core.domain.licenses.PhilipsLicenses;
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
        return DtoConverter.toDto(project);
    }

    @Override
    public ProjectDto project(UUID projectId) {
        final var project = validProject(projectId);
        return DtoConverter.toDto(project);
    }

    @Override
    public void importSpdx(UUID projectId, InputStream stream) {
        final var project = validProject(projectId);
        new SpdxParser(project).parse(stream);

        //TODO Temp experiment
        new LicenseChecker(PhilipsLicenses.REGISTRY, project).verify().forEach(System.out::println);
    }

    @Override
    public List<PackageDto> packages(UUID projectId) {
        final var project = validProject(projectId);

        return DtoConverter.toDtoList(project.getPackages());
    }

    private Project validProject(UUID projectId) {
        return store.readProject(projectId)
                .orElseThrow(() -> new NotFoundException("project", projectId));
    }
}

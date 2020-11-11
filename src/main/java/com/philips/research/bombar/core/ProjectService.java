/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.core;

import pl.tlinkowski.annotation.basic.NullOr;

import java.io.InputStream;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ProjectService {
    /**
     * @return all projects
     */
    List<ProjectDto> projects();

    /**
     * Creates a new project.
     *
     * @param title Assigned name of the project
     * @return project definition
     */
    ProjectDto createProject(@NullOr String title);

    /**
     * @return the indicated project
     */
    ProjectDto getProject(UUID projectId);

    /**
     * Updates project settings.
     *
     * @param dto updated fields for project
     * @return result of the update
     */
    ProjectDto updateProject(ProjectDto dto);

    /**
     * Updates a project from an SPDX tag-value file.
     *
     * @param stream file content
     */
    void importSpdx(UUID projectId, InputStream stream);

    /**
     * @return all package of the indicated project
     */
    List<DependencyDto> getDependencies(UUID projectId);

    /**
     * @return the indicated package
     */
    DependencyDto getDependency(UUID projectId, String dependencyId);

    class ProjectDto {
        public final UUID id;
        public @NullOr String title;
        public @NullOr Instant updated;
        public @NullOr String distribution;
        public @NullOr String phase;
        public int issues;
        public @NullOr List<DependencyDto> packages;

        public ProjectDto(UUID id) {
            this.id = id;
        }
    }

    class DependencyDto {
        public final String id;
        public @NullOr URI purl;
        public String title = "";
        public String version = "";
        public @NullOr String license;
        public @NullOr String relation;
        public PackageService.@NullOr PackageDto pkg;
        public int issues;
        public @NullOr List<String> violations;
        public @NullOr List<DependencyDto> dependencies;
        public @NullOr List<DependencyDto> usages;

        public DependencyDto(String id) {
            this.id = id;
        }
    }
}


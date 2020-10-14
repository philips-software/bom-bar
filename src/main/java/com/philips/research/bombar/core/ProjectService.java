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
    ProjectDto createProject(String title);

    /**
     * @return the indicated project
     */
    ProjectDto getProject(UUID projectId);

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
    DependencyDto getDependency(UUID projectId, URI reference);

    class ProjectDto {
        public @NullOr String title = "";
        public @NullOr UUID id;
        public int issues;
        public @NullOr List<DependencyDto> packages;
    }

    class DependencyDto {
        public @NullOr String reference;
        public @NullOr String title;
        public @NullOr String version;
        public @NullOr String license;
        public @NullOr String relation;
        public int issues;
        public @NullOr List<DependencyDto> dependencies;
    }
}


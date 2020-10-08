/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.collector.core;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface ProjectService {
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
    ProjectDto project(UUID projectId);

    /**
     * Updates a project from an SPDX tag-value file.
     *
     * @param stream file content
     */
    void importSpdx(UUID projectId, InputStream stream);

    /**
     * @return all package of the indicated project
     */
    List<PackageDto> packages(UUID projectId);

    class ProjectDto {
        public String title;
        public UUID id;
        public List<PackageDto> packages;
    }

    class PackageDto {
        public String reference;
        public String title;
        public String license;
        public String relation;
        public List<PackageDto> children;
    }
}


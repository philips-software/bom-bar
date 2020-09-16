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
    }

    class PackageDto {
        public String reference;
        public String title;
        public String version;
        public List<PackageDto> children;
        public String license;
    }
}


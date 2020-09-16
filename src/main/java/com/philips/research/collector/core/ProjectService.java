package com.philips.research.collector.core;

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


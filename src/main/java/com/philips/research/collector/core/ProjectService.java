package com.philips.research.collector.core;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    ProjectDto createProject(String name);

    List<PackageDto> packages(UUID projectId);

    class ProjectDto {
        public String name;
        public UUID uuid;
    }

    class PackageDto {
        public String type;
        public String namespace;
        public String name;
        public String version;
        public List<PackageDto> children;
        public String license;
    }
}


package com.philips.research.collector.core.domain;

import java.util.Optional;
import java.util.UUID;

public interface ProjectStore {
    Project createProject();

    Optional<Project> readProject(UUID uuid);

    Package createPackage(String name, String version);
}

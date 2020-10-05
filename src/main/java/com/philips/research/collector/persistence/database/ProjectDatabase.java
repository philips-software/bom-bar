/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.persistence.database;

import com.philips.research.collector.core.domain.*;
import org.springframework.stereotype.Repository;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProjectDatabase implements ProjectStore {

    private final Map<UUID, Project> projects = new HashMap<>();
    private final Map<String, PackageDefinition> packages = new HashMap<>();

    @Override
    public Project createProject() {
        final var uuid = UUID.randomUUID();
        final var project = new Project(uuid);
        projects.put(uuid, project);
        return project;
    }

    @Override
    public Optional<Project> readProject(UUID uuid) {
        final var project = projects.get(uuid);
        return Optional.ofNullable(project);
    }

    @Override
    public Dependency createDependency(@NullOr PackageDefinition pkg, String version) {
        return new Dependency(pkg, version);
    }

    @Override
    public Relation createRelation(Relation.Type type, Dependency target) {
        return new Relation(type, target);
    }

    @Override
    public PackageDefinition getOrCreatePackageDefinition(String reference) {
        return packages.computeIfAbsent(reference, PackageDefinition::new);
    }
}

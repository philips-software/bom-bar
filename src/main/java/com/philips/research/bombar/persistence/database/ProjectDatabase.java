/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.persistence.database;

import com.philips.research.bombar.core.domain.*;
import org.springframework.stereotype.Repository;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.*;

@Repository
public class ProjectDatabase implements ProjectStore {

    private final Map<UUID, Project> projects = new HashMap<>();
    private final Map<String, PackageDefinition> packages = new HashMap<>();

    @Override
    public List<Project> getProjects() {
        return new ArrayList<>(projects.values());
    }

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

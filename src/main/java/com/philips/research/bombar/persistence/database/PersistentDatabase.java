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

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class PersistentDatabase implements PersistentStore {

    private final Map<UUID, Project> projects = new HashMap<>();
    private final Map<URI, PackageDefinition> packages = new HashMap<>();

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
    public Dependency createDependency(String id, String title) {
        return new Dependency(id, title);
    }

    @Override
    public Relation createRelation(Relation.Relationship type, Dependency target) {
        return new Relation(type, target);
    }

    @Override
    public PackageDefinition createPackageDefinition(URI reference) {
        final var pkg = new PackageDefinition(reference);
        packages.put(reference, pkg);
        return pkg;
    }

    @Override
    public Optional<PackageDefinition> getPackageDefinition(URI reference) {
        return Optional.ofNullable(packages.get(reference));
    }

    @Override
    public List<PackageDefinition> findPackageDefinitions(String fragment) {
        final var matcher = fragment.toLowerCase();
        return packages.values().stream()
                .filter(pkg -> pkg.getReference().toString().toLowerCase().contains(matcher))
                .limit(50)
                .collect(Collectors.toList());
    }
}
/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.core.domain;

import java.util.*;
import java.util.stream.Collectors;

public class Project {
    private final UUID id;
    private final List<Dependency> dependencies = new ArrayList<>();
    private String title;
    private Distribution distribution = Distribution.OPEN_SOURCE;
    private Phase phase = Phase.DEVELOPMENT;

    public Project(UUID id) {
        this.id = id;
        title = id.toString();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Project setTitle(String title) {
        this.title = title;
        return this;
    }

    public List<Dependency> getRootDependencies() {
        final var roots = new ArrayList<>(dependencies);
        dependencies.stream()
                .flatMap(dep -> dep.getRelations().stream())
                .forEach(rel -> roots.remove(rel.getTarget()));
        Collections.sort(roots);
        return roots;
    }

    public List<Dependency> getDependencies() {
        return this.dependencies.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public Optional<Dependency> getDependency(PackageDefinition pkg, String version) {
        return dependencies.stream()
                .filter(dep -> dep.isEqualTo(pkg, version))
                .findFirst();
    }

    public Project addDependency(Dependency dependency) {
        dependencies.add(dependency);
        return this;
    }

    public Project clearDependencies() {
        dependencies.clear();
        return this;
    }

    public Distribution getDistribution() {
        return distribution;
    }

    public Project setDistribution(Distribution distribution) {
        this.distribution = distribution;
        return this;
    }

    public Phase getPhase() {
        return phase;
    }

    public Project setPhase(Phase phase) {
        this.phase = phase;
        return this;
    }

    public enum Distribution {
        OPEN_SOURCE,
        INTERNAL,
        SAAS,
        PROPRIETARY
    }

    public enum Phase {
        DEVELOPMENT,
        RELEASED
    }
}

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

import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class Project {
    private final UUID id;
    private final Map<String, Dependency> dependencies = new HashMap<>();
    private final Map<URI, String> packageExemptions = new HashMap<>();
    private String title;
    private @NullOr Instant lastUpdate;
    private Distribution distribution = Distribution.PROPRIETARY;
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

    public Optional<Instant> getLastUpdate() {
        return Optional.ofNullable(lastUpdate);
    }

    public Project setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    public Project exempt(URI reference, String rationale) {
        packageExemptions.put(reference, rationale);
        return this;
    }

    public boolean isExempted(URI reference) {
        return packageExemptions.containsKey(reference);
    }

    public List<Exemption<URI>> getExemptions() {
        return packageExemptions.entrySet().stream()
                .map(entry -> new Exemption<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public Collection<Dependency> getRootDependencies() {
        final var roots = new ArrayList<>(dependencies.values());
        dependencies.values().stream()
                .flatMap(dep -> dep.getRelations().stream())
                .forEach(rel -> roots.remove(rel.getTarget()));
        return roots;
    }

    public Collection<Dependency> getDependencies() {
        return dependencies.values();
    }

    public Optional<Dependency> getDependency(String id) {
        return Optional.ofNullable(dependencies.get(id));
    }

    public Project addDependency(Dependency dependency) {
        final var id = dependency.getId();
        if (dependencies.containsKey(id)) {
            throw new DomainException(String.format("Project %s contains duplicate dependency %s", this.id, id));
        }
        dependencies.put(id, dependency);
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

    public int getIssueCount() {
        return dependencies.values().stream().mapToInt(Dependency::getIssueCount).sum();
    }

    public Phase getPhase() {
        return phase;
    }

    public Project setPhase(Phase phase) {
        this.phase = phase;
        return this;
    }

    @Override
    public String toString() {
        return id.toString();
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

/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain;

import pl.tlinkowski.annotation.basic.NullOr;

import java.time.Instant;
import java.util.*;

public class Project {
    private final UUID uuid;
    private final Map<String, Dependency> dependencies = new HashMap<>();
    // Key is package reference, value is rationale of exemption
    private final Map<PackageRef, String> packageExemptions = new HashMap<>();
    private String title = "";
    private @NullOr Instant lastUpdate;
    private int issueCount;
    private Distribution distribution = Distribution.PROPRIETARY;
    private Phase phase = Phase.DEVELOPMENT;

    public Project(UUID uuid) {
        this.uuid = uuid;
    }

    public Project postProcess() {
        issueCount = dependencies.values().stream().mapToInt(Dependency::getIssueCount).sum();
        getRootDependencies().forEach(Dependency::setRoot);

        return this;
    }

    public UUID getId() {
        return uuid;
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

    public Project exempt(Dependency dependency, String rationale) {
        dependency.getPackageReference().ifPresent(reference -> {
            packageExemptions.put(reference, rationale);
            dependencies.values().stream()
                    .filter(dep -> dep.getPackageReference().stream().anyMatch(ref -> ref.equals(reference)))
                    .forEach(dep -> dep.setExemption(rationale));
        });
        return this;
    }

    public Project unexempt(Dependency dependency) {
        dependency.getPackageReference().ifPresent(reference -> {
            packageExemptions.remove(reference);
            dependencies.values().stream()
                    .filter(dep -> dep.getPackageReference().stream().anyMatch(ref -> ref.equals(reference)))
                    .forEach(dep -> dep.setExemption(null));
        });
        return this;
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
        final var id = dependency.getKey();
        if (dependencies.containsKey(id)) {
            throw new DomainException(String.format("Project %s contains duplicate dependency %s", this.uuid, id));
        }
        setExemptions(dependency);
        dependencies.put(id, dependency);
        return this;
    }

    public Project addRelationship(Dependency parent, Dependency child, Relation.Relationship relationship) {
        validateDependency(parent);
        validateDependency(child);

        parent.addRelation(new Relation(relationship, child));
        child.addUsage(parent);
        return this;
    }

    private void validateDependency(Dependency dependency) {
        if (!dependencies.containsValue(dependency)) {
            throw new DomainException("Dependency " + dependency + " is not part of project " + this);
        }
    }

    private void setExemptions(Dependency dependency) {
        dependency.getPackageReference()
                .flatMap(key -> Optional.ofNullable(packageExemptions.get(key)))
                .ifPresent(dependency::setExemption);
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
        return issueCount;
    }

    public Phase getPhase() {
        return phase;
    }

    public Project setPhase(Phase phase) {
        this.phase = phase;
        return this;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return getId().equals(project.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return String.format("%s: '%s'", uuid, title);
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

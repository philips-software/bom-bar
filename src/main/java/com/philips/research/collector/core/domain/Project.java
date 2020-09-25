package com.philips.research.collector.core.domain;

import java.util.*;
import java.util.stream.Collectors;

public class Project {
    private final UUID id;
    private final List<Package> packages = new ArrayList<>();
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

    public List<Package> getRootPackages() {
        final var roots = new ArrayList<>(packages);
        packages.stream()
                .flatMap(pkg -> pkg.getChildren().stream())
                .forEach(child -> roots.remove(child.getPackage()));
        Collections.sort(roots);
        return roots;
    }

    public List<Package> getPackages() {
        return packages.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public Optional<Package> getPackage(String name, String version) {
        return packages.stream()
                .filter(pkg -> pkg.getReference().equals(name) && pkg.getVersion().equals(version))
                .findFirst();
    }

    public Project addPackage(Package pkg) {
        packages.add(pkg);
        return this;
    }

    public Project removePackage(Package pkg) {
        packages.remove(pkg);
        packages.forEach(p -> p.removeChild(pkg));
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

package com.philips.research.collector.core.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Project {
    private final String id;
    private final List<Package> packages = new ArrayList<>();
    private Distribution distribution = Distribution.OPEN_SOURCE;
    private Phase phase = Phase.DEVELOPMENT;

    public Project(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<Package> getPackages() {
        return packages.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public Project addPackage(Package pkg) {
        packages.add(pkg);
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
        NONE,
        INTERNAL,
        SAAS,
        PROPRIETARY,
        OPEN_SOURCE
    }

    public enum Phase {
        DEVELOPMENT,
        RELEASED
    }
}

package com.philips.research.collector.core.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Package implements Comparable<Package> {
    private final String name;
    private final String version;
    private final List<Package> children = new ArrayList<>();
    private Package parent;
    private Relation relation = Relation.STATIC_LINK;
    private String concludedLicense;
    private License license;
    private LicenseExemption exemption;
    private boolean isUpdated;

    public Package(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public Optional<Package> getParent() {
        return Optional.ofNullable(parent);
    }

    Package orphan() {
        if (parent != null) {
            parent.children.remove(this);
            parent = null;
        }
        return this;
    }

    public Package addChild(Package pkg) {
        pkg.orphan();
        children.add(pkg);
        pkg.parent = this;

        return this;
    }

    public List<Package> getChildren() {
        return children.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public Relation getRelation() {
        return relation;
    }

    public Package setRelation(Relation relation) {
        this.relation = relation;
        return this;
    }

    public String getConcludedLicense() {
        return concludedLicense;
    }

    public Package setConcludedLicense(String concludedLicense) {
        this.concludedLicense = concludedLicense;
        return this;
    }

    public License getLicense() {
        return license;
    }

    public Package setLicense(License license) {
        this.license = license;
        return this;
    }

    public Optional<LicenseExemption> getExemption() {
        return Optional.ofNullable(exemption);
    }

    public Package setExemption(LicenseExemption exemption) {
        this.exemption = exemption;
        return this;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public Package setUpdated(boolean updated) {
        isUpdated = updated;
        return this;
    }

    @Override
    public int compareTo(Package other) {
        return Comparator.comparing(Package::getName)
                .thenComparing(Package::getVersion)
                .compare(this, other);
    }

    @Override
    public String toString() {
        return name + "-" + version;
    }

    public enum Relation {
        FORBIDDEN,
        UNRELATED,
        INDEPENDENT,
        DYNAMIC_LINK,
        STATIC_LINK,
        SOURCE_CODE
    }
}

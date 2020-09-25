package com.philips.research.collector.core.domain;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.*;
import java.util.stream.Collectors;

public final class Package implements Comparable<Package> {
    private final String reference;
    private final String version;
    private final List<Link> links = new ArrayList<>();

    private String title;
    private String license = "";
    private @NullOr LicenseExemption exemption;
    private boolean isUpdated;

    public Package(String reference, String version) {
        this.reference = reference;
        this.title = reference;
        this.version = version;
    }

    public String getReference() {
        return reference;
    }

    public String getVersion() {
        return version;
    }

    public String getTitle() {
        return title;
    }

    public Package setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getLicense() {
        return license;
    }

    public Package setLicense(String license) {
        this.license = license;
        return this;
    }

    public Package addChild(Package pkg, Relation relation) {
        links.add(new Link(pkg, relation));

        return this;
    }

    public List<Link> getChildren() {
        return links.stream()
                .sorted(Comparator.comparing(Link::getPackage))
                .collect(Collectors.toList());
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
        return Comparator.comparing(Package::getReference)
                .thenComparing(Package::getVersion)
                .compare(this, other);
    }

    @Override
    public boolean equals(@NullOr Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Package aPackage = (Package) o;
        return Objects.equals(reference, aPackage.reference) &&
                Objects.equals(version, aPackage.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference, version);
    }

    @Override
    public String toString() {
        return reference + "-" + version;
    }

    public enum Relation {
        UNRELATED,
        INDEPENDENT,
        DYNAMIC_LINK,
        STATIC_LINK,
        SOURCE_CODE
    }

    public enum Exemption {
        PASSED,
        REQUIRED,
        FAILED
    }

    public static class Link {
        private final Package pkg;
        private final Relation relation;

        public Link(Package pkg, Relation relation) {
            this.pkg = pkg;
            this.relation = relation;
        }

        public Package getPackage() {
            return pkg;
        }

        public Relation getRelation() {
            return relation;
        }
    }
}

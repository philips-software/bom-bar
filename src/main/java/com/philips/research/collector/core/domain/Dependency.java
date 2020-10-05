/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class Dependency implements Comparable<Dependency> {
    private final @NullOr PackageDefinition pkg;
    private final String version;
    private final List<Relation> relations = new ArrayList<>();

    private String title = "";
    private String license = "";
    private @NullOr LicenseExemption exemption;

    public Dependency(@NullOr PackageDefinition pkg, String version) {
        this.pkg = pkg;
        this.version = version;
    }

    public Optional<PackageDefinition> getPackage() {
        return Optional.ofNullable(pkg);
    }

    public String getVersion() {
        return version;
    }

    public String getLicense() {
        return license;
    }

    public Dependency setLicense(String license) {
        this.license = license;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Dependency setTitle(String title) {
        this.title = title;
        return this;
    }

    public Dependency addRelation(Relation relation) {
        relations.add(relation);
        return this;
    }

    public List<Relation> getRelations() {
        return relations.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public Optional<LicenseExemption> getExemption() {
        return Optional.ofNullable(exemption);
    }

    public Dependency setExemption(LicenseExemption exemption) {
        this.exemption = exemption;
        return this;
    }

    public boolean isEqualTo(PackageDefinition pkg, String version) {
        return (pkg.equals(this.pkg)) && this.version.equals(version);
    }

    @Override
    public int compareTo(Dependency other) {
        //noinspection ConstantConditions
        if (pkg == null && other.pkg == null) {
            return version.compareTo(other.version);
        }
        if (pkg == null) {
            return -1;
        }
        if (other.pkg == null) {
            return 1;
        }
        //noinspection ConstantConditions
        return Comparator.comparing((Dependency d) -> d.pkg)
                .thenComparing(Dependency::getVersion)
                .compare(this, other);
    }

    @Override
    public String toString() {
        return pkg + "@" + version;
    }

    public enum Exemption {
        PASSED,
        REQUIRED,
        FAILED
    }
}

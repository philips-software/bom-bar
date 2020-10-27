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
import java.util.*;

public final class Dependency {
    private final String id;
    private final String title;
    private final List<Relation> relations = new ArrayList<>();
    private final List<Dependency> usages = new ArrayList<>();

    private @NullOr PackageDefinition pkg;
    private String version = "";
    private String license = "";
    private int issueCount;
    private @NullOr LicenseExemption exemption;

    public Dependency(@NullOr String id, String title) {
        this.id = (id != null) ? id : UUID.randomUUID().toString();
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Optional<PackageDefinition> getPackage() {
        return Optional.ofNullable(pkg);
    }

    public Dependency setPackage(PackageDefinition pkg) {
        this.pkg = pkg;
        return this;
    }

    public Optional<URI> getPackageUrl() {
        final @NullOr URI purl = (pkg != null)
                ? URI.create("pkg:" + pkg.getReference() + (!version.isBlank() ? '@' + version : ""))
                : null;
        return Optional.ofNullable(purl);
    }

    public String getVersion() {
        return version;
    }

    public Dependency setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getLicense() {
        return license;
    }

    public Dependency setLicense(String license) {
        this.license = license;
        return this;
    }

    public int getIssueCount() {
        return issueCount;
    }

    public Dependency setIssueCount(int issueCount) {
        this.issueCount = issueCount;
        return this;
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public Dependency addRelation(Relation relation) {
        relations.add(relation);
        return this;
    }

    public List<Dependency> getUsages() {
        return usages;
    }

    public Dependency addUsage(Dependency dependency) {
        this.usages.add(dependency);
        return this;
    }

    public Optional<LicenseExemption> getExemption() {
        return Optional.ofNullable(exemption);
    }

    public Dependency setExemption(LicenseExemption exemption) {
        this.exemption = exemption;
        return this;
    }

    @Override
    public final boolean equals(@NullOr Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependency that = (Dependency) o;
        return id.equals(that.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s: '%s'", id, title);
    }

    public enum Exemption {
        PASSED,
        REQUIRED,
        FAILED
    }
}

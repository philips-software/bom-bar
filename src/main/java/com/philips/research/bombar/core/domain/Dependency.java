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

public class Dependency {
    private final String key;
    private final String title;
    private final Set<Relation> relations = new HashSet<>();
    private final Set<Dependency> usages = new HashSet<>();

    private @NullOr PackageDefinition pkg;
    private String version = "";
    private String license = "";
    private int issueCount;
    private @NullOr String exemption;

    public Dependency(@NullOr String key, String title) {
        this.key = (key != null) ? key : UUID.randomUUID().toString();
        this.title = title;
    }

    public String getKey() {
        return key;
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

    public Optional<URI> getPackageReference() {
        return Optional.ofNullable((pkg != null) ? pkg.getReference() : null);
    }

    public Optional<URI> getPackageUrl() {
        return getPackage()
                .map(pkg -> URI.create("pkg:" + pkg.getReference() + (!version.isBlank() ? '@' + version : "")));
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

    public Collection<Relation> getRelations() {
        return relations;
    }

    public Dependency addRelation(Relation relation) {
        relations.add(relation);
        return this;
    }

    public Collection<Dependency> getUsages() {
        return usages;
    }

    public Dependency addUsage(Dependency dependency) {
        this.usages.add(dependency);
        return this;
    }

    public Optional<String> getExemption() {
        return Optional.ofNullable(exemption);
    }

    public Dependency setExemption(@NullOr String description) {
        exemption = description;
        return this;
    }

    @Override
    public final boolean equals(@NullOr Object o) {
        if (this == o) return true;
        if (!(o instanceof Dependency)) return false;
        Dependency that = (Dependency) o;
        return key.equals(that.key);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return String.format("%s: '%s'", key, title);
    }
}

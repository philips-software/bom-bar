/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain;

import com.github.packageurl.PackageURL;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.*;
import java.util.stream.Collectors;

public class Dependency {
    private final String key;
    private final String title;
    private final Set<Relation> relations = new HashSet<>();
    private final Set<Dependency> usages = new HashSet<>();

    private @NullOr Package pkg;
    private @NullOr PackageURL purl;
    private String version = "";
    private String license = "";
    private boolean isRoot;
    private boolean isDevelopment;
    private boolean isDelivered;
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

    public Optional<PackageURL> getPurl() {
        return Optional.ofNullable(purl);
    }

    public Dependency setPurl(PackageURL purl) {
        this.purl = purl;
        return this;
    }

    public Optional<Package> getPackage() {
        return Optional.ofNullable(pkg);
    }

    public Dependency setPackage(Package pkg) {
        this.pkg = pkg;
        return this;
    }

    public Optional<PackageRef> getPackageReference() {
        return Optional.ofNullable((pkg != null) ? pkg.getReference() : null);
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

    public List<String> getLicenses() {
        final var licenses = license
                .split("\\s*(AND|OR|\\)|\\()\\s*");
        return Arrays.stream(licenses)
                .filter(l -> !l.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean isRoot() {
        return this.isRoot;
    }

    public Dependency setRoot() {
        this.isRoot = true;
        this.isDelivered = true;
        return this;
    }

    public boolean isDevelopment() {
        return isDevelopment;
    }

    Dependency setDevelopment() {
        isDevelopment = true;
        return this;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    Dependency setDelivered() {
        isDelivered = true;
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

    Dependency addRelation(Relation relation) {
        relations.add(relation);
        return this;
    }

    public Collection<Dependency> getUsages() {
        return usages;
    }

    Dependency addUsage(Dependency dependency) {
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

    public Optional<Relation.Relationship> getStrongestUsage() {
        return Optional.ofNullable(getUsages().stream()
                .flatMap(parent -> parent.getRelations().stream())
                .filter(relation -> relation.getTarget() == this)
                .map(Relation::getType)
                .reduce(null, (previous, next) -> (previous == null || next.compareTo(previous) > 0) ? next : previous));
    }

    @Override
    public final boolean equals(@NullOr Object o) {
        if (this == o) return true;
        if (!(o instanceof Dependency)) return false;
        Dependency that = (Dependency) o;
        return getKey().equals(that.getKey());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getKey());
    }

    @Override
    public String toString() {
        return String.format("%s: '%s'", key, title);
    }
}

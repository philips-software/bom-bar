/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain;

import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Package implements Comparable<Package> {
    private final PackageRef reference;
    private final Set<String> exemptedLicenses = new HashSet<>();

    private String name;
    private @NullOr String vendor;
    private @NullOr URI homepage;
    private @NullOr String description;
    private Acceptance acceptance = Acceptance.DEFAULT;

    public Package(PackageRef reference) {
        this.reference = reference;
        name = reference.toString();
    }

    public PackageRef getReference() {
        return reference;
    }

    public String getName() {
        return name;
    }

    public Package setName(String name) {
        this.name = name;
        return this;
    }

    public Optional<String> getVendor() {
        return Optional.ofNullable(vendor);
    }

    public Package setVendor(@NullOr String vendor) {
        this.vendor = vendor;
        return this;
    }

    public Optional<URI> getHomepage() {
        return Optional.ofNullable(homepage);
    }

    public Package setHomepage(@NullOr URI homepage) {
        this.homepage = homepage;
        return this;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public Package setDescription(String description) {
        this.description = description;
        return this;
    }

    public Acceptance getAcceptance() {
        return acceptance;
    }

    public Package setAcceptance(Acceptance acceptance) {
        this.acceptance = acceptance;
        return this;
    }

    /**
     * Explicitly allows a license for this package.
     */
    public Package exemptLicense(String license) {
        exemptedLicenses.add(license);
        return this;
    }

    /**
     * Removes allowance of a license for this package.
     */
    public void removeLicenseExemption(String license) {
        exemptedLicenses.remove(license);
    }

    /**
     * @return true if the given license is explicitly allowed for this package
     */
    public boolean isLicenseExempted(String license) {
        return exemptedLicenses.stream().anyMatch((lic) -> lic.equalsIgnoreCase(license));
    }

    /**
     * @return All current license exemptions
     */
    public Set<String> getLicenseExemptions() {
        return exemptedLicenses;
    }

    @Override
    public int compareTo(Package other) {
        return reference.compareTo(other.reference);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Package)) return false;
        Package that = (Package) o;
        return getReference().equals(that.getReference());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getReference());
    }

    @Override
    public String toString() {
        return reference.toString();
    }

    public enum Acceptance {
        DEFAULT, APPROVED, FORBIDDEN, PER_PROJECT, NOT_A_PACKAGE
    }
}

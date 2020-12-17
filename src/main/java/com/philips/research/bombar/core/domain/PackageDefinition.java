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
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PackageDefinition implements Comparable<PackageDefinition> {
    private final URI reference;
    private final Set<String> exemptedLicenses = new HashSet<>();

    private String name;
    private @NullOr String vendor;
    private @NullOr URL homepage;
    private Acceptance acceptance = Acceptance.DEFAULT;

    public PackageDefinition(URI reference) {
        this.reference = reference;
        name = reference.toString();
    }

    public URI getReference() {
        return reference;
    }

    public String getName() {
        return name;
    }

    public PackageDefinition setName(String name) {
        this.name = name;
        return this;
    }

    public Optional<String> getVendor() {
        return Optional.ofNullable(vendor);
    }

    public PackageDefinition setVendor(@NullOr String vendor) {
        this.vendor = vendor;
        return this;
    }

    public Optional<URL> getHomepage() {
        return Optional.ofNullable(homepage);
    }

    public PackageDefinition setHomepage(@NullOr URL homepage) {
        this.homepage = homepage;
        return this;
    }

    public Acceptance getAcceptance() {
        return acceptance;
    }

    public PackageDefinition setAcceptance(Acceptance acceptance) {
        this.acceptance = acceptance;
        return this;
    }

    /**
     * Explicitly allows a license for this package.
     */
    public PackageDefinition exemptLicense(String license) {
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
        return exemptedLicenses.stream().anyMatch((lic)->lic.equalsIgnoreCase(license));
    }

    /**
     * @return All current license exemptions
     */
    public Set<String> getLicenseExemptions() {
        return exemptedLicenses;
    }

    @Override
    public int compareTo(PackageDefinition other) {
        return reference.compareTo(other.reference);
    }

    @Override
    public String toString() {
        return reference.toString();
    }

    public enum Acceptance {
        DEFAULT, APPROVED, FORBIDDEN, PER_PROJECT, NOT_A_PACKAGE
    }
}

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PackageDefinition implements Comparable<PackageDefinition> {
    private final URI reference;
    private final List<Exemption> licenseExemptions = new ArrayList<>();

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
    public PackageDefinition exemptLicense(String license, String rationale) {
        licenseExemptions.add(new Exemption(license, rationale));
        return this;
    }

    /**
     * Removes allowance of a license for this package.
     */
    public void removeLicenseExemption(String license) {
        licenseExemptions.removeIf(ex -> ex.getKey().equals(license));
    }

    /**
     * @return true if the given license is explicitly allowed for this package
     */
    public boolean isLicenseExempted(String license) {
        return licenseExemptions.stream().anyMatch(ex -> ex.getKey().equals(license));
    }

    /**
     * @return All current license exemptions
     */
    public List<Exemption> getLicenseExemptions() {
        return licenseExemptions;
    }

    @Override
    public int compareTo(PackageDefinition other) {
        return reference.compareTo(other.reference);
    }

    @Override
    public String toString() {
        return reference.toString();
    }

    enum Acceptance {
        DEFAULT, APPROVED, FORBIDDEN, PER_PROJECT
    }
}

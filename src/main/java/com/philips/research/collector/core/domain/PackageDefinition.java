/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain;

import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URL;
import java.util.Optional;

public class PackageDefinition implements Comparable<PackageDefinition> {
    private final String reference;

    private String name;
    private @NullOr String vendor;
    private @NullOr URL homepage;

    public PackageDefinition(String reference) {
        this.reference = reference;
        final var parts = reference.split("/");
        name = parts[parts.length - 1];
    }

    public String getReference() {
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

    public PackageDefinition setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    @Override
    public int compareTo(PackageDefinition other) {
        return reference.compareToIgnoreCase(other.reference);
    }

    @Override
    public String toString() {
        return reference;
    }
}

/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain;

import com.philips.research.bombar.core.domain.Package.Acceptance;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class PackageTest {
    private static final PackageRef REFERENCE = new PackageRef("Type/Namespace/Name");
    private static final String LICENSE = "License";
    private static final URI HOMEPAGE = URI.create("https://example.com");
    private static final String VENDOR = "Vendor name";
    private static final String DESCRIPTION = "Description";

    private final Package pkg = new Package(REFERENCE);

    @Test
    void createsInstanceWithDefaultName() {
        assertThat(pkg.getReference()).isEqualTo(REFERENCE);
        assertThat(pkg.getName()).isEqualTo(REFERENCE.toString());
        assertThat(pkg.getVendor()).isEmpty();
        assertThat(pkg.getHomepage()).isEmpty();
        assertThat(pkg.getDescription()).isEmpty();
        assertThat(pkg.getAcceptance()).isEqualTo(Acceptance.DEFAULT);
    }

    @Test
    void updatesAcceptance() {
        pkg.setAcceptance(Acceptance.PER_PROJECT);

        assertThat(pkg.getAcceptance()).isEqualTo(Acceptance.PER_PROJECT);
    }

    @Test
    void exemptsLicensesIgnoringCasing() {
        pkg.exemptLicense(LICENSE);

        assertThat(pkg.isLicenseExempted(LICENSE.toLowerCase())).isTrue();
        assertThat(pkg.isLicenseExempted(LICENSE.toUpperCase())).isTrue();
        assertThat(pkg.isLicenseExempted("Other")).isFalse();
        assertThat(pkg.getLicenseExemptions()).contains(LICENSE);
    }

    @Test
    void dropsLicenseExemption() {
        pkg.exemptLicense(LICENSE);

        pkg.removeLicenseExemption(LICENSE);

        assertThat(pkg.isLicenseExempted(LICENSE)).isFalse();
    }

    @Test
    void updatesPackageDetails() throws Exception {
        pkg.setHomepage(HOMEPAGE);
        pkg.setVendor(VENDOR);
        pkg.setDescription(DESCRIPTION);

        assertThat(pkg.getHomepage()).contains(HOMEPAGE);
        assertThat(pkg.getVendor()).contains(VENDOR);
        assertThat(pkg.getDescription()).contains(DESCRIPTION);
    }

    @Test
    void implementsComparable() {
        final var one = new Package(new PackageRef("One"));
        final var two = new Package(new PackageRef("Two"));

        //noinspection EqualsWithItself
        assertThat(one.compareTo(one)).isEqualTo(0);
        assertThat(one.compareTo(two)).isNegative();
        assertThat(two.compareTo(one)).isPositive();
    }

    @Test
    void implementsEquals() {
        EqualsVerifier.forClass(Package.class)
                .withOnlyTheseFields("reference")
                .withNonnullFields("reference")
                .verify();
    }
}

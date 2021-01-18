/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.core.domain.licenses;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LicenseRegistryTest {
    private static final String TAG_A = "Tag A";
    private static final String TAG_B = "Tag B";
    private static final String LICENSE = "License";
    private static final String PARENT = "Parent";
    private final LicenseRegistry registry = new LicenseRegistry();
    private final Term termA = registry.term(TAG_A, "A");
    private final Term termB = registry.term(TAG_B, "B");

    @Test
    void registersTerm() {
        assertThat(registry.getTerms()).containsExactlyInAnyOrder(termA, termB);
    }

    @Test
    void throws_duplicateTerm() {
        assertThatThrownBy(() -> registry.term(TAG_A.toUpperCase(), "Description"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("duplicate");
    }

    @Test
    void registersLicenseByName() {
        registry.license("abc");
        registry.license("xyzzy");

        assertThat(registry.getLicenses()).containsExactly("abc", "xyzzy");
    }

    @Test
    void throws_registerDuplicateLicense() {
        registry.license(LICENSE);

        assertThatThrownBy(() -> registry.license(LICENSE.toUpperCase()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("duplicate");
    }

    @Test
    void registersDerivedLicense() {
        final var parent = registry.license(PARENT).require(TAG_A);

        registry.license(LICENSE, parent).require(TAG_B);

        final var type = registry.licenseType(LICENSE);
        assertThat(type.requiresGiven()).containsExactlyInAnyOrder(termA, termB);
    }

    @Test
    void registersDerivedLicenseWithException() {
        final var base = registry.license(LICENSE).require(TAG_A);

        registry.with("Exception", base).require(TAG_B);

        final var type = registry.licenseType(LICENSE + " WITH Exception");
        assertThat(type.requiresGiven()).contains(termA, termB);
    }

    private enum Condition {NO, YES}

    @Nested
    class Compatibility {
        private static final String OTHER = "Other";

        @Test
        void addsConditionalDemandToLicense() {
            registry.license(LICENSE).demands(TAG_A, Condition.YES);

            var type = registry.licenseType(LICENSE);
            assertThat(type.demandsGiven(Condition.YES)).contains(termA);
            assertThat(type.demandsGiven(Condition.NO)).isEmpty();
        }

        @Test
        void addsAcceptedTerm() {
            registry.license(LICENSE).accepts(TAG_A);

            var type = registry.licenseType(LICENSE);
            assertThat(type.accepts()).containsExactly(termA);
        }

        @Test
        void addsAcceptedOtherLicense() {
            final var builder = registry.license(OTHER).copyleft();

            registry.license(LICENSE).accepts(builder);

            final var type = registry.licenseType(LICENSE);
            final var other = registry.licenseType(OTHER);
            assertThat(type.unmetDemands(other)).isEmpty();
        }

        @Test
        void registersWeakCopyleftLicense() {
            registry.license(OTHER);

            registry.license(LICENSE).copyleft(Condition.YES);

            final var type = registry.licenseType(LICENSE);
            final var other = registry.licenseType(OTHER);
            assertThat(type.unmetDemands(type, Condition.YES)).isEmpty();
            assertThat(other.unmetDemands(type, Condition.YES)).isNotEmpty();
            assertThat(other.unmetDemands(type, Condition.NO)).isEmpty();
        }

        @Test
        void makesLicenseCopyLeftAliasOfOtherLicense() {
            final var otherBuilder = registry.license(OTHER).copyleft();

            registry.license(LICENSE).copyleft(otherBuilder);

            final var type = registry.licenseType(LICENSE);
            final var other = registry.licenseType(OTHER);
            assertThat(type.unmetDemands(type)).isEmpty();
            assertThat(type.unmetDemands(other)).isEmpty();
            assertThat(other.unmetDemands(type)).isEmpty();
        }

        @Test
        void makesLicenseExplicitlyCompatibleWithTargetLicense() {
            final var otherBuilder = registry.license(OTHER).copyleft();

            registry.license(LICENSE).copyleft().compatibleWith(otherBuilder);

            final var type = registry.licenseType(LICENSE);
            final var other = registry.licenseType(OTHER);
            assertThat(other.unmetDemands(type)).isEmpty();
            assertThat(type.unmetDemands(other)).isNotEmpty();
        }

        @Test
        void inheritsCopyleftAndCompatibility() {
            final var otherBuilder = registry.license(OTHER).copyleft();
            final var parentBuilder = registry.license(PARENT).copyleft()
                    .compatibleWith(otherBuilder);

            registry.license(LICENSE, parentBuilder);

            final var type = registry.licenseType(LICENSE);
            final var other = registry.licenseType(OTHER);
            final var parent = registry.licenseType(PARENT);
            assertThat(parent.unmetDemands(type)).isEmpty();
            assertThat(type.unmetDemands(parent)).isEmpty();
            assertThat(other.unmetDemands(type)).isEmpty();
            assertThat(type.unmetDemands(other)).isNotEmpty();
        }
    }
}

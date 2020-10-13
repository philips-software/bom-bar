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
    private final LicenseRegistry registry = new LicenseRegistry();

    private enum Condition {YES}

    @Nested
    class BuildRegistry {
        @Test
        void registersTerm() {
            final var attr1 = registry.term(TAG_A, "A");
            final var attr2 = registry.term(TAG_B, "B");

            assertThat(registry.getTerms()).containsExactlyInAnyOrder(attr1, attr2);
        }

        @Test
        void throws_duplicateTerm() {
            registry.term(TAG_A, "Description");
            assertThatThrownBy(() -> registry.term(TAG_A.toUpperCase(), "Description"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("duplicate");
        }

        @Test
        void registersLicense() {
            registry.license("x");
            registry.license("zzz");
            registry.license("Y");

            assertThat(registry.getLicenses()).containsExactly("x", "Y", "zzz");
        }

        @Test
        void throws_duplicateLicense() {
            registry.license(LICENSE);
            assertThatThrownBy(() -> registry.license(LICENSE.toUpperCase()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("duplicate");
        }

        @Test
        void registersInheritedLicense() {
            final var attrA = registry.term(TAG_A, "Description");
            final var attrB = registry.term(TAG_B, "Description");
            registry.license("Parent").require(TAG_A);
            registry.license(LICENSE, "Parent").require(TAG_B);

            final var type = registry.licenseType(LICENSE);

            assertThat(type.requiredGiven()).containsExactlyInAnyOrder(attrA, attrB);
        }

        @Test
        void throws_ParentUnknown() {
            assertThatThrownBy(() -> registry.license(LICENSE, "Parent"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Parent")
                    .hasMessageContaining("Unknown reference");
        }

        @Test
        void demandsTerm() {
            final var term = registry.term(TAG_A, "Test");

            registry.license(LICENSE).demand(TAG_A, Condition.YES);

            var type = registry.licenseType(LICENSE);
            assertThat(type.demandsGiven(Condition.YES)).containsExactly(term);
        }

        @Test
        void acceptsTerm() {
            final var term = registry.term(TAG_A, "Test");

            registry.license(LICENSE).accept(TAG_A);

            var type = registry.licenseType(LICENSE);
            assertThat(type.accepts()).containsExactly(term);
        }

        @Test
        void acceptsLicense() {
            final var other = registry.license("Other");

            registry.license(LICENSE).accept(other);

            var type = registry.licenseType(LICENSE);
            final var term = registry.getTerms().iterator().next();
            assertThat(type.accepts()).containsExactly(term);
        }

        @Test
        void makesLicenseWeakCopyleft() {
            registry.license(LICENSE).copyleft(Condition.YES);

            final var type = registry.licenseType(LICENSE);

            assertThat(type.demandsGiven(Condition.YES)).hasSize(1);
            final var term = type.demandsGiven(Condition.YES).iterator().next();
            assertThat(term.getTag()).isEqualTo(LICENSE);
            assertThat(term.getDescription()).contains(LICENSE);
            assertThat(type.accepts()).containsExactly(term);
        }

        @Test
        void makesLicenseAliasedCopyLeft() {
            final var other = registry.license("Other");
            registry.license(LICENSE).copyleft(other);

            final var type = registry.licenseType(LICENSE);

            assertThat(type.demandsGiven()).hasSize(1);
            final var term = type.demandsGiven().iterator().next();
            assertThat(term.getTag()).isEqualTo("Other");
            assertThat(term.getDescription()).contains("Other");
            assertThat(type.accepts()).containsExactly(term);
        }
    }
}

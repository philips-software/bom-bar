/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain.licenses;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LicenseRegistryTest {
    private static final String TAG_A = "Tag A";
    private static final String TAG_B = "Tag B";
    private static final String LICENSE = "License";

    private final LicenseRegistry registry = new LicenseRegistry();

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
        void throwIfParentUnknown() {
            assertThatThrownBy(() -> registry.license(LICENSE, "Parent"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Parent")
                    .hasMessageContaining("Unknown reference");
        }
    }
}

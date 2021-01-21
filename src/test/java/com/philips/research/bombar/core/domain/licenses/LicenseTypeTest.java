/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain.licenses;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LicenseTypeTest {
    private static final String NAME = "Name";
    private static final Term TERM_A = new Term("A", "Term A");
    private static final Term TERM_B = new Term("B", "Term B");
    private static final Term TERM_C = new Term("C", "Term C");

    @Test
    void createsInstance() {
        final var type = new LicenseType(NAME)
                .require(TERM_A)
                .demand(TERM_B)
                .accept(TERM_C);

        assertThat(type.getIdentifier()).isEqualTo(NAME);
        assertThat(type.requiresGiven()).containsExactly(TERM_A);
        assertThat(type.demandsGiven()).containsExactly(TERM_B);
        assertThat(type.accepts()).containsExactly(TERM_C);
    }

    @Test
    void createsInheritedInstance() {
        final var parent = new LicenseType("Parent")
                .require(TERM_A)
                .demand(TERM_B)
                .accept(TERM_C);
        final var child = new LicenseType("Child", parent);

        final var derived = new LicenseType(NAME, child);

        assertThat(derived.getIdentifier()).isEqualTo(NAME);
        assertThat(derived.requiresGiven()).containsExactly(TERM_A);
        assertThat(derived.demandsGiven()).containsExactly(TERM_B);
        assertThat(derived.accepts()).containsExactly(TERM_C);
    }

    @Test
    void overridesInheritedSimpleConditions() {
        final var parent = new LicenseType("Parent")
                .require(TERM_A);
        final var child = new LicenseType("Child", parent)
                .require(TERM_A, Condition.THRESHOLD);

        assertThat(child.requiresGiven(Condition.NO)).isEmpty();
        assertThat(child.requiresGiven(Condition.YES)).contains(TERM_A);
    }

    @Test
    void overridesInheritedLicenseConditions() {
        final var license = new LicenseType("License");
        final var parent = new LicenseType("Parent")
                .require(Term.from(license), Condition.YES);
        final var inheritedLicenseTerm = Term.from(new LicenseType("Inherited", license));
        final var child = new LicenseType("Child", parent)
                .require(inheritedLicenseTerm, Condition.THRESHOLD);

        assertThat(child.requiresGiven(Condition.NO)).isEmpty();
        assertThat(child.requiresGiven(Condition.YES)).containsExactly(inheritedLicenseTerm);
    }

    @Test
    void filtersRequiredTerms() {
        final var type = new LicenseType(NAME)
                .require(TERM_A)
                .require(TERM_B, Condition.THRESHOLD);

        assertThat(type.requiresGiven()).containsExactlyInAnyOrder(TERM_A, TERM_B);
        assertThat(type.requiresGiven(Condition.NO)).containsExactly(TERM_A);
        assertThat(type.requiresGiven(Condition.YES)).containsExactlyInAnyOrder(TERM_A, TERM_B);
    }

    @Test
    void filtersDemandedTerms() {
        final var type = new LicenseType(NAME)
                .demand(TERM_A)
                .demand(TERM_B, Condition.THRESHOLD);

        assertThat(type.demandsGiven()).containsExactlyInAnyOrder(TERM_A, TERM_B);
        assertThat(type.demandsGiven(Condition.NO)).containsExactly(TERM_A);
        assertThat(type.demandsGiven(Condition.YES)).containsExactlyInAnyOrder(TERM_A, TERM_B);
    }

    @Test
    void tracksAcceptedTerms() {
        final var type = new LicenseType(NAME)
                .accept(TERM_A);

        assertThat(type.accepts()).containsExactlyInAnyOrder(TERM_A);
    }

    private enum Condition {NO, THRESHOLD, YES}

    @Nested
    class ConflictDetection {
        private final LicenseType type = new LicenseType(NAME);
        private final LicenseType other = new LicenseType(NAME);

        @Test
        void noConflictForCompatibleLicenses() {
            type.accept(TERM_A);
            other.demand(TERM_A);

            assertThat(type.unmetDemands(other)).isEmpty();
        }

        @Test
        void listsConflictsWithOtherLicense() {
            type.accept(TERM_A);
            other.demand(TERM_A).demand(TERM_B);

            assertThat(type.unmetDemands(other)).containsExactly(TERM_B);
        }

        @Test
        void listsConditionalConflictsWithOtherLicense() {
            other.demand(TERM_A, Condition.NO).demand(TERM_B, Condition.YES);

            assertThat(type.unmetDemands(other, Condition.NO)).containsExactly(TERM_A);
        }
    }
}

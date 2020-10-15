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
        assertThat(type.requiredGiven()).containsExactly(TERM_A);
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
        assertThat(derived.requiredGiven()).containsExactly(TERM_A);
        assertThat(derived.demandsGiven()).containsExactly(TERM_B);
        assertThat(derived.accepts()).containsExactly(TERM_C);
    }

    @Test
    void overridesInheritedConditions() {
        final var parent = new LicenseType("Parent")
                .require(TERM_A, Condition.YES)
                .demand(TERM_B);
        final var child = new LicenseType("Child", parent)
                .require(TERM_A)
                .demand(TERM_B, Condition.YES);

        assertThat(child.requiredGiven(Condition.NO)).contains(TERM_A);
        assertThat(child.demandsGiven(Condition.NO)).doesNotContain(TERM_B);
    }

    @Test
    void filtersRequiredTerms() {
        final var type = new LicenseType(NAME)
                .require(TERM_A)
                .require(TERM_B, Condition.THRESHOLD);

        assertThat(type.requiredGiven()).containsExactlyInAnyOrder(TERM_A);
        assertThat(type.requiredGiven(Condition.NO)).containsExactly(TERM_A);
        assertThat(type.requiredGiven(Condition.YES)).containsExactlyInAnyOrder(TERM_A, TERM_B);
    }

    @Test
    void filtersDemandedTerms() {
        final var type = new LicenseType(NAME)
                .demand(TERM_A)
                .demand(TERM_B, Condition.THRESHOLD);

        assertThat(type.demandsGiven()).containsExactlyInAnyOrder(TERM_A);
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

            assertThat(type.incompatibilities(other)).isEmpty();
        }

        @Test
        void listsConflictsWithOtherLicense() {
            type.accept(TERM_A);
            other.demand(TERM_A).demand(TERM_B);

            assertThat(type.incompatibilities(other)).containsExactly(TERM_B);
        }

        @Test
        void listsConditionalConflictsWithOtherLicense() {
            other.demand(TERM_A, Condition.NO).demand(TERM_B, Condition.YES);

            assertThat(type.incompatibilities(other, Condition.NO)).containsExactly(TERM_A);
        }
    }
}

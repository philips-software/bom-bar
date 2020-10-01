/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain.licenses;

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
                .forbid(TERM_B);

        assertThat(type.getIdentifier()).isEqualTo(NAME);
        assertThat(type.requiredGiven()).containsExactly(TERM_A);
        assertThat(type.forbiddenGiven()).containsExactly(TERM_B);
    }

    @Test
    void createsInheritedInstance() {
        final var type = new LicenseType("Parent")
                .require(TERM_A)
                .forbid(TERM_B);
        final var child = new LicenseType("Child", type);

        final var derived = new LicenseType(NAME, child);

        assertThat(derived.getIdentifier()).isEqualTo(NAME);
        assertThat(derived.requiredGiven()).containsExactly(TERM_A);
        assertThat(derived.forbiddenGiven()).containsExactly(TERM_B);
    }

    @Test
    void filtersRequiredTerms() {
        final var type = new LicenseType(NAME)
                .require(TERM_A)
                .require(TERM_B, Condition.MID);

        assertThat(type.requiredGiven()).containsExactlyInAnyOrder(TERM_A);
        assertThat(type.requiredGiven(Condition.LOW)).containsExactly(TERM_A);
        assertThat(type.requiredGiven(Condition.HIGH)).containsExactlyInAnyOrder(TERM_A, TERM_B);
    }

    @Test
    void filtersForbiddenTerms() {
        final var type = new LicenseType(NAME)
                .forbid(TERM_A)
                .forbid(TERM_B, Condition.MID);

        assertThat(type.forbiddenGiven()).containsExactlyInAnyOrder(TERM_A);
        assertThat(type.forbiddenGiven(Condition.LOW)).containsExactly(TERM_A);
        assertThat(type.forbiddenGiven(Condition.HIGH)).containsExactlyInAnyOrder(TERM_A, TERM_B);
    }

    @Test
    void listsConditionalConflictsWithOtherLicense() {
        final var type = new LicenseType(NAME)
                .require(TERM_A, Condition.LOW)
                .require(TERM_B, Condition.LOW)
                .forbid(TERM_C, Condition.HIGH);
        final var other = new LicenseType(NAME)
                .require(TERM_A, Condition.LOW)
                .forbid(TERM_B, Condition.LOW)
                .require(TERM_C, Condition.HIGH);

        final var conflicts = type.conflicts(other, Condition.MID);

        assertThat(conflicts).containsExactly(TERM_B);
    }

    private enum Condition {LOW, MID, HIGH}
}

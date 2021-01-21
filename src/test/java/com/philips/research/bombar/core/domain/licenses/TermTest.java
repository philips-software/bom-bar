/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain.licenses;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TermTest {
    private static final String TAG = "Tag";
    private static final int NUMBER = 42;
    private static final LicenseType LICENSE = new LicenseType("License");
    private static final String DESCRIPTION = "Description";

    @Test
    void createsObjectInstance() {
        final var term = new Term(TAG, DESCRIPTION);

        assertThat(term.getKey()).isEqualTo(TAG);
        assertThat(term.getDescription()).isEqualTo(DESCRIPTION);
    }

    @Test
    void createsLicenseTypeInstance() {
        final var term = Term.from(LICENSE);

        assertThat(term.getKey()).isEqualTo(LICENSE);
        assertThat(term.getDescription()).contains(LICENSE.getIdentifier()).isNotEqualTo(LICENSE.getIdentifier());
    }

    @Test
    void matchesOnKeyEquality() {
        final var term = new Term(TAG, DESCRIPTION);
        final var other = new Term(42, DESCRIPTION);

        assertThat(term.isMatching(List.of(other, term))).isTrue();
        assertThat(term.isMatching(List.of(other))).isFalse();
    }

    @Test
    void matchesOnLicenseAncestor() {
        final var parent = new Term(LICENSE, DESCRIPTION);
        final var child = Term.from(new LicenseType("Child", LICENSE));

        assertThat(parent.isMatching(List.of(parent))).isTrue();
        assertThat(parent.isMatching(List.of(child))).isTrue();
        assertThat(child.isMatching(List.of(parent))).isFalse();
    }
}

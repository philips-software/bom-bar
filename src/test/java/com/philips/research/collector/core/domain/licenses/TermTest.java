/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain.licenses;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TermTest {
    private static final String TAG = "Tag";
    private static final String DESCRIPTION = "Description";

    @Test
    void createsInstance() {
        final var term = new Term(TAG, DESCRIPTION);

        assertThat(term.getTag()).isEqualTo(TAG);
        assertThat(term.getDescription()).isEqualTo(DESCRIPTION);
    }
}

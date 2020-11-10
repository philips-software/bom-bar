/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.core.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExemptionTest {
    private static final String KEY = "Key";
    private static final String RATIONALE = "Rationale";

    @Test
    void createsInstance() {
        final var exemption = new Exemption(KEY, RATIONALE);

        assertThat(exemption.getKey()).isEqualTo(KEY);
        assertThat(exemption.getRationale()).isEqualTo(RATIONALE);
    }
}

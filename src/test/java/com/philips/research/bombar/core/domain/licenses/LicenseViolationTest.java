/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain.licenses;

import com.philips.research.bombar.core.domain.Dependency;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LicenseViolationTest {
    static final Dependency DEPENDENCY = new Dependency("Id", "Title");
    private static final String MESSAGE = "Message";

    @Test
    void createsInstance() {
        final var violation = new LicenseViolation(DEPENDENCY, MESSAGE);

        assertThat(violation.getDependency()).isEqualTo(DEPENDENCY);
        assertThat(violation.getMessage()).isEqualTo(MESSAGE);
        assertThat(violation.toString()).contains(DEPENDENCY.toString());
        assertThat(violation.toString()).contains(MESSAGE);
    }
}

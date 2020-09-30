/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain.licenses;

import com.philips.research.collector.core.domain.Package;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LicenseViolationTest {
    static final Package PACKAGE = new Package("Package", "Version");
    private static final String MESSAGE = "Message";

    @Test
    void createsInstance() {
        final var violation = new LicenseViolation(PACKAGE, MESSAGE);

        assertThat(violation.getPkg()).isEqualTo(PACKAGE);
        assertThat(violation.toString()).contains(PACKAGE.toString());
        assertThat(violation.toString()).contains(MESSAGE);
    }
}

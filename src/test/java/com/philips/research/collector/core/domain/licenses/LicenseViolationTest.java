/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.collector.core.domain.licenses;

import com.philips.research.collector.core.domain.Dependency;
import com.philips.research.collector.core.domain.PackageDefinition;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LicenseViolationTest {
    static final Dependency DEPENDENCY = new Dependency(new PackageDefinition("Package"), "Version");
    private static final String MESSAGE = "Message";

    @Test
    void createsInstance() {
        final var violation = new LicenseViolation(DEPENDENCY, MESSAGE);

        assertThat(violation.getDependency()).isEqualTo(DEPENDENCY);
        assertThat(violation.toString()).contains(DEPENDENCY.toString());
        assertThat(violation.toString()).contains(MESSAGE);
    }
}

/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PackageDefinitionTest {
    private static final String REFERENCE = "Type/Namespace/Name";

    @Test
    void createsInstanceWithDefaultName() {
        PackageDefinition pkg = new PackageDefinition(REFERENCE);

        assertThat(pkg.getReference()).isEqualTo(REFERENCE);
        assertThat(pkg.getName()).isEqualTo("Name");
    }

    @Test
    void implementsComparable() {
        final var one = new PackageDefinition("One");
        final var two = new PackageDefinition("Two");

        //noinspection EqualsWithItself
        assertThat(one.compareTo(one)).isEqualTo(0);
        assertThat(one.compareTo(two)).isNegative();
        assertThat(two.compareTo(one)).isPositive();
    }
}

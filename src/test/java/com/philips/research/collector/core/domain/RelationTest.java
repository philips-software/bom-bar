/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RelationTest {
    private static final PackageDefinition PACKAGE = new PackageDefinition("Reference");
    private static final String VERSION = "Version";

    final Dependency dependency = new Dependency(PACKAGE, VERSION);

    @Test
    void createsInstance() {
        final var relation = new Relation(Relation.Type.DYNAMIC_LINK, dependency);

        assertThat(relation.getType()).isEqualTo(Relation.Type.DYNAMIC_LINK);
        assertThat(relation.getTarget()).isEqualTo(dependency);
    }

    @Test
    void implementsComparable() {
        final var dep1 = new Dependency(PACKAGE, "Aaa");
        final var relation1 = new Relation(Relation.Type.UNRELATED, dep1);
        final var dep2 = new Dependency(PACKAGE, "Bbb");
        final var relation2 = new Relation(Relation.Type.UNRELATED, dep2);

        //noinspection EqualsWithItself
        assertThat(relation1).isEqualByComparingTo(relation1);
        assertThat(relation1).isLessThan(relation2);
        assertThat(relation2).isGreaterThan(relation1);
    }
}

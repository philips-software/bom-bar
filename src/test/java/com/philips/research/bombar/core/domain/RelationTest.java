/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RelationTest {
    final Dependency dependency = new Dependency("Id", "Title");

    @Test
    void createsInstance() {
        final var relation = new Relation(Relation.Relationship.DYNAMIC_LINK, dependency);

        assertThat(relation.getType()).isEqualTo(Relation.Relationship.DYNAMIC_LINK);
        assertThat(relation.getTarget()).isEqualTo(dependency);
    }

    @Test
    void implementsEquals() {
        EqualsVerifier.forClass(Relation.class)
                .withNonnullFields("type", "target")
                .withPrefabValues(Dependency.class, new Dependency("red", "Red"), new Dependency("black", "Black"))
                .verify();
    }
}

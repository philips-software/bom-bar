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

class RelationTest {
    final Dependency dependency = new Dependency("Id", "Title");

    @Test
    void createsInstance() {
        final var relation = new Relation(Relation.Type.DYNAMIC_LINK, dependency);

        assertThat(relation.getType()).isEqualTo(Relation.Type.DYNAMIC_LINK);
        assertThat(relation.getTarget()).isEqualTo(dependency);
    }
}

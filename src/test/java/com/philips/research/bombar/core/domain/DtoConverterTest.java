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

class DtoConverterTest {
    private static final String VERSION = "Version";

    @Test
    void cutsCyclicDependencies() {
        final var self = new Dependency(null, VERSION);
        self.addRelation(new Relation(Relation.Type.DYNAMIC_LINK, self));

        final var dto = DtoConverter.toDto(self);

        assert dto.dependencies != null;
        assertThat(dto.dependencies).hasSize(1);
        assertThat(dto.dependencies.get(0).dependencies).isEmpty();
    }
}

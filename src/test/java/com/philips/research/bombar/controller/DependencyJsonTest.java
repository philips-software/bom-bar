/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.controller;

import com.philips.research.bombar.core.ProjectService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DependencyJsonTest {
    private static final String REFERENCE = "Reference";
    private static final String TITLE = "Title";
    private static final String LICENSE = "License";
    private static final String RELATION = "Relation";
    private static final String VIOLATION = "Violation";

    @Nested
    class CreateFromDto {
        @Test
        void createsInstanceFromDto() {
            final var dto = new ProjectService.DependencyDto();
            dto.reference = REFERENCE;
            dto.title = TITLE;
            dto.license = LICENSE;
            dto.relation = RELATION;

            final var json = new DependencyJson(dto);

            assertThat(json.id).isEqualTo(REFERENCE);
            assertThat(json.title).isEqualTo(TITLE);
            assertThat(json.relation).isEqualTo(RELATION);
            assertThat(json.dependencies).isNull();
        }

        @Test
        void includesViolationsFromDto() {
            final var dto = new ProjectService.DependencyDto();
            dto.violations = List.of(VIOLATION);

            final var json = new DependencyJson(dto);

            assertThat(json.licenseIssues).isNotEmpty();
        }

        @Test
        void convertsNullListToNull() {
            assertThat(DependencyJson.toList(null)).isNull();
        }

        @Test
        void convertsList() {
            final var list = List.of(new ProjectService.DependencyDto());

            assertThat(DependencyJson.toList(list)).isNotEmpty();
        }
    }
}

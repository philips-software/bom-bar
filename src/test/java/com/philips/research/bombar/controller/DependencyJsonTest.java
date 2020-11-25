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

import com.philips.research.bombar.core.PackageService;
import com.philips.research.bombar.core.ProjectService.DependencyDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DependencyJsonTest {
    private static final URI PURL = URI.create("pkg:namespace/name@version");
    private static final String ID = "Id";
    private static final String TITLE = "Title";
    private static final String LICENSE = "License";
    private static final String RELATION = "Relation";
    private static final String VIOLATION = "Violation";
    private static final URI REFERENCE = URI.create("Reference");

    @Nested
    class CreateFromDto {
        @Test
        void createsInstanceFromDto() {
            final var dto = new DependencyDto(ID);
            dto.title = TITLE;
            dto.purl = PURL;
            dto.license = LICENSE;
            dto.relation = RELATION;
            dto.pkg = new PackageService.PackageDto();
            dto.pkg.reference = REFERENCE;

            final var json = new DependencyJson(dto);

            assertThat(json.id).isEqualTo(ID);
            assertThat(json.title).isEqualTo(TITLE);
            assertThat(json.purl).isEqualTo(PURL);
            assertThat(json.relation).isEqualTo(RELATION);
            assertThat(json.dependencies).isNull();
            assertThat(json.usages).isNull();
            //noinspection ConstantConditions
            assertThat(json.pkg).isNotNull();
        }

        @Test
        void includesRelations() {
            final var dto = new DependencyDto(ID);
            dto.dependencies = List.of(new DependencyDto(ID), new DependencyDto(ID));
            dto.usages = List.of(new DependencyDto(ID));

            final var json = new DependencyJson(dto);

            assertThat(json.dependencies).hasSize(2);
            assertThat(json.usages).hasSize(1);
        }

        @Test
        void includesViolationsFromDto() {
            final var dto = new DependencyDto(ID);
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
            final var list = List.of(new DependencyDto(ID));

            assertThat(DependencyJson.toList(list)).isNotEmpty();
        }
    }
}

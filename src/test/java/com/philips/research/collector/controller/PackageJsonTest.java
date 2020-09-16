package com.philips.research.collector.controller;

import com.philips.research.collector.core.ProjectService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PackageJsonTest {
    private static final String REFERENCE = "Reference";
    private static final String TITLE = "Title";
    private static final String LICENSE = "License";
    private static final String RELATION = "Relation";

    @Nested
    class CreateFromDto {
        @Test
        void createsInstanceFromDto() {
            final var dto = new ProjectService.PackageDto();
            dto.reference = REFERENCE;
            dto.title = TITLE;
            dto.license = LICENSE;
            dto.relation = RELATION;

            final var json = new PackageJson(dto);

            assertThat(json.id).isEqualTo(REFERENCE);
            assertThat(json.title).isEqualTo(TITLE);
            assertThat(json.relation).isEqualTo(RELATION);
            assertThat(json.children).isNull();
        }

        @Test
        void convertsNullListToNull() {
            assertThat(PackageJson.toList(null)).isNull();
        }

        @Test
        void convertsList() {
            final var list = List.of(new ProjectService.PackageDto());

            assertThat(PackageJson.toList(list)).isNotEmpty();
        }
    }
}

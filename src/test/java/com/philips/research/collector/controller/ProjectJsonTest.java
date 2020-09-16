package com.philips.research.collector.controller;

import com.philips.research.collector.core.ProjectService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectJsonTest {
    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final String TITLE = "Title";

    @Test
    void createsInstanceFromDto() {
        final var dto = new ProjectService.ProjectDto();
        dto.id = PROJECT_ID;
        dto.title = TITLE;

        final var json = new ProjectJson(dto);
        assertThat(json.id).isEqualTo(PROJECT_ID);
        assertThat(json.title).isEqualTo(TITLE);
        assertThat(json.packages).isNull();
    }

    @Test
    void includesPackagesFromDto() {
        final var dto = new ProjectService.ProjectDto();
        dto.packages = List.of(new ProjectService.PackageDto());

        final var json = new ProjectJson(dto);

        assertThat(json.packages).isNotEmpty();
    }
}

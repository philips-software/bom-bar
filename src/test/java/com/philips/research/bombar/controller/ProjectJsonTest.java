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
import org.junit.jupiter.api.Test;
import pl.tlinkowski.annotation.basic.NullOr;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectJsonTest {
    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final String ID = "Id";
    private static final String TITLE = "Title";
    private static final Instant LAST_UPDATED = Instant.now();
    private static final String DISTRIBUTION = "Distribution";
    private static final String PHASE = "Phase";

    @Test
    void createsInstanceFromDto() {
        final var dto = new ProjectService.ProjectDto(PROJECT_ID);
        dto.title = TITLE;
        dto.updated = LAST_UPDATED;
        dto.distribution = DISTRIBUTION;
        dto.phase = PHASE;
        dto.exemptions = List.of("Exemption", "Another");

        final var json = new ProjectJson(dto);

        assert json.id != null;
        assertThat(json.id).isEqualTo(PROJECT_ID);
        assertThat(json.title).isEqualTo(TITLE);
        assertThat(json.updated).isEqualTo(LAST_UPDATED);
        assertThat(json.distribution).isEqualTo(DISTRIBUTION);
        assertThat(json.phase).isEqualTo(PHASE);
        assertThat(json.packages).isNull();
        assertThat(json.exemptions).hasSize(2);
    }

    @Test
    void includesPackagesFromDto() {
        final var dto = new ProjectService.ProjectDto(PROJECT_ID);
        dto.packages = List.of(new ProjectService.DependencyDto(ID));

        final var json = new ProjectJson(dto);

        assertThat(json.packages).isNotEmpty();
    }

    @Test
    void convertsNullListToNull() {
        assertThat(ProjectJson.toList(null)).isNull();
    }

    @Test
    void convertsDtoList() {
        final var dto = new ProjectService.ProjectDto(PROJECT_ID);

        final @NullOr List<ProjectJson> result = ProjectJson.toList(List.of(dto));

        assert result != null;
        assertThat(Objects.requireNonNull(result.get(0).id)).isEqualTo(PROJECT_ID);
    }

    @Test
    void convertsToDto() {
        final var json = new ProjectJson();
        json.title = TITLE;
        json.distribution = DISTRIBUTION;
        json.phase = PHASE;

        final var dto = json.toDto(PROJECT_ID);

        assertThat(dto.id).isEqualTo(PROJECT_ID);
        assertThat(dto.title).isEqualTo(TITLE);
        assertThat(dto.distribution).isEqualTo(DISTRIBUTION);
        assertThat(dto.phase).isEqualTo(PHASE);
    }
}

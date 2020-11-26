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

import com.philips.research.bombar.core.ProjectService.ProjectDto;
import pl.tlinkowski.annotation.basic.NullOr;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

class ProjectJson {
    @NullOr UUID id;
    @NullOr String title;
    @NullOr Instant updated;
    @NullOr String distribution;
    @NullOr String phase;
    int issues;
    @NullOr List<DependencyJson> packages;
    @NullOr List<String> exemptions;

    @SuppressWarnings("unused")
    ProjectJson() {
    }

    ProjectJson(ProjectDto dto) {
        this.id = dto.id;
        this.title = dto.title;
        this.updated = dto.updated;
        this.distribution = dto.distribution;
        this.phase = dto.phase;
        this.issues = dto.issues;
        this.packages = DependencyJson.toList(dto.packages);
        this.exemptions = dto.exemptions;
    }

    static @NullOr List<ProjectJson> toList(@NullOr List<ProjectDto> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(ProjectJson::new)
                .collect(Collectors.toList());
    }

    ProjectDto toDto(UUID id) {
        final var dto = new ProjectDto(id);
        dto.title = this.title;
        dto.distribution = this.distribution;
        dto.phase = this.phase;
        return dto;
    }
}

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
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

class ProjectJson {
    @NullOr UUID id;
    @NullOr String title;
    int issues;
    @NullOr List<IssueJson> licenseIssues;
    @NullOr List<DependencyJson> packages;

    @SuppressWarnings("unused")
    ProjectJson() {
    }

    ProjectJson(ProjectService.ProjectDto dto) {
        this.id = dto.id;
        this.title = dto.title;
        this.issues = dto.issues;
        this.licenseIssues = IssueJson.toList(dto.violations);
        this.packages = DependencyJson.toList(dto.packages);
    }

    static @NullOr List<ProjectJson> toList(@NullOr List<ProjectService.ProjectDto> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(ProjectJson::new)
                .collect(Collectors.toList());
    }
}

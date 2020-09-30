/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.controller;

import com.philips.research.collector.core.ProjectService;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("NotNullFieldNotInitialized")
class ProjectJson {
    UUID id;
    String title;
    @NullOr List<PackageJson> packages;

    @SuppressWarnings("unused")
    ProjectJson() {
    }

    ProjectJson(ProjectService.ProjectDto dto) {
        this.id = dto.id;
        this.title = dto.title;
        this.packages = PackageJson.toList(dto.packages);
    }
}

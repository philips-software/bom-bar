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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

class DependencyJson {
    @NullOr String id;
    @NullOr String title;
    @NullOr String version;
    @NullOr String license;
    @NullOr String relation;
    int issues;
    @NullOr List<DependencyJson> dependencies;

    @SuppressWarnings("unused")
    DependencyJson() {
    }

    DependencyJson(ProjectService.DependencyDto dto) {
        if (dto.reference != null) {
            this.id = URLEncoder.encode(dto.reference, StandardCharsets.UTF_8);
        }
        this.title = dto.title;
        this.version = dto.version;
        this.relation = dto.relation;
        this.license = dto.license;
        this.issues = dto.issues;
        this.dependencies = toList(dto.dependencies);
    }

    static @NullOr List<DependencyJson> toList(@NullOr List<ProjectService.DependencyDto> dtoList) {
        if (dtoList == null) {
            return null;
        }

        return dtoList.stream().map(DependencyJson::new).collect(Collectors.toList());
    }
}

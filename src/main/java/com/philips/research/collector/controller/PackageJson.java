/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.controller;

import com.philips.research.collector.core.ProjectService;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("NotNullFieldNotInitialized")
class PackageJson {
    String id;
    String title;
    String license;
    String relation;
    @NullOr List<PackageJson> children;

    @SuppressWarnings("unused")
    PackageJson() {
    }

    PackageJson(ProjectService.PackageDto dto) {
        this.id = dto.reference;
        this.title = dto.title;
        this.relation = dto.relation;
        this.license = dto.license;
        this.children = toList(dto.children);
    }

    static @NullOr List<PackageJson> toList(@NullOr List<ProjectService.PackageDto> dtoList) {
        if (dtoList == null) {
            return null;
        }

        return dtoList.stream().map(PackageJson::new).collect(Collectors.toList());
    }
}

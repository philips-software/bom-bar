/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.philips.research.bombar.core.ProjectService;
import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

class DependencyJson {
    @NullOr String id;
    @NullOr String title;
    @NullOr URI purl;
    @NullOr String version;
    @NullOr String license;
    @NullOr String relation;
    @JsonProperty("package")
    @NullOr PackageJson pkg;
    boolean source;
    int issues;
    @NullOr List<String> licenseIssues;
    @NullOr List<DependencyJson> dependencies;
    @NullOr List<DependencyJson> usages;
    @NullOr String exemption;

    @SuppressWarnings("unused")
    DependencyJson() {
    }

    DependencyJson(ProjectService.DependencyDto dto) {
        this.id = dto.id;
        this.purl = dto.purl;
        this.title = dto.title;
        this.version = dto.version;
        this.relation = dto.relation;
        this.license = dto.license;
        this.pkg = PackageJson.fromDto(dto.pkg);
        this.source = dto.source;
        this.issues = dto.issues;
        this.licenseIssues = dto.violations;
        this.dependencies = toList(dto.dependencies);
        this.usages = toList(dto.usages);
        this.exemption = dto.exemption;
    }

    static @NullOr List<DependencyJson> toList(@NullOr List<ProjectService.DependencyDto> dtoList) {
        if (dtoList == null) {
            return null;
        }

        return dtoList.stream().map(DependencyJson::new).collect(Collectors.toList());
    }
}

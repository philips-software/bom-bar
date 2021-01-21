/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.controller;

import com.philips.research.bombar.core.PackageService.PackageDto;
import com.philips.research.bombar.core.ProjectService;
import pl.tlinkowski.annotation.basic.NullOr;

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

class PackageJson {
    @NullOr String id;
    @NullOr URI reference;
    @NullOr String name;
    @NullOr String vendor;
    @NullOr URL homepage;
    @NullOr String description;
    @NullOr String approval;
    @NullOr List<String> exemptions;
    @NullOr List<ProjectJson> projects;

    PackageJson(PackageDto dto) {
        this.id = encode(encode(dto.reference.toString()));
        this.reference = dto.reference;
        this.name = dto.name;
        this.vendor = dto.vendor;
        this.homepage = dto.homepage;
        this.description = dto.description;
        this.approval = dto.approval.toString().toLowerCase();
        this.exemptions = dto.licenseExemptions;
    }

    private static String encode(String string) {
        return URLEncoder.encode(string, StandardCharsets.UTF_8);
    }

    public static @NullOr PackageJson fromDto(@NullOr PackageDto pkg) {
        return (pkg != null) ? new PackageJson(pkg) : null;
    }

    public static List<PackageJson> toList(List<PackageDto> list) {
        return list.stream()
                .map(PackageJson::new)
                .collect(Collectors.toList());
    }

    public PackageJson setProjects(List<ProjectService.ProjectDto> projects) {
        this.projects = ProjectJson.toList(projects);
        return this;
    }
}


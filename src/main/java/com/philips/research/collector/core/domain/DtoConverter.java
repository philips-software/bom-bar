/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain;

import com.philips.research.collector.core.ProjectService;

import java.util.List;
import java.util.stream.Collectors;

abstract class DtoConverter {
    static ProjectService.ProjectDto toDto(Project project) {
        final var dto = new ProjectService.ProjectDto();
        dto.id = project.getId();
        dto.title = project.getTitle();
        dto.packages = toDtoList(project.getRootDependencies());
        return dto;
    }

    static List<ProjectService.PackageDto> toDtoList(List<Dependency> dependencies) {
        return dependencies.stream()
                .map(DtoConverter::toDto)
                .collect(Collectors.toList());
    }

    static ProjectService.PackageDto toDto(Dependency dependency) {
        // Add once there are more elaborate details
        return baseDto(dependency);
    }

    static ProjectService.PackageDto toDto(Relation relation) {
        final var target = relation.getTarget();
        final var dto = baseDto(target);
        dto.relation = relation.getType().name().toLowerCase();
        return dto;
    }

    private static ProjectService.PackageDto baseDto(Dependency dependency) {
        final var dto = new ProjectService.PackageDto();
        dependency.getPackage().ifPresent(pkg -> dto.reference = pkg.getReference() + "@" + dependency.getVersion());
        dto.title = dependency.getTitle() + " version " + dependency.getVersion();
        dto.license = dependency.getLicense();
        dto.children = dependency.getRelations().stream()
                .map(DtoConverter::toDto)
                .collect(Collectors.toList());
        return dto;
    }
}

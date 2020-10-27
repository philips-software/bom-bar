/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.core.domain;

import com.philips.research.bombar.core.ProjectService;
import com.philips.research.bombar.core.domain.licenses.LicenseViolation;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.List;
import java.util.stream.Collectors;

abstract class DtoConverter {
    static ProjectService.ProjectDto toDto(Project project) {
        final var dto = toBaseDto(project);
        dto.packages = project.getDependencies().stream()
                .map(DtoConverter::toBaseDto)
                .sorted(DtoConverter::alphabetic)
                .collect(Collectors.toList());
        return dto;
    }

    static ProjectService.ProjectDto toBaseDto(Project project) {
        final var dto = new ProjectService.ProjectDto();
        dto.id = project.getId();
        dto.title = project.getTitle();
        dto.updated = project.getLastUpdate().orElse(null);
        dto.issues = project.getIssueCount();
        return dto;
    }

    static ProjectService.DependencyDto toDto(Dependency dependency, List<LicenseViolation> violations) {
        // Add once there are more elaborate details
        final var dto = toBaseDto(dependency);
        dto.violations = violations.stream().map(LicenseViolation::getMessage).collect(Collectors.toList());
        dto.dependencies = dependency.getRelations().stream()
                .map(DtoConverter::toDto)
                .sorted(DtoConverter::alphabetic)
                .collect(Collectors.toList());
        dto.usages = dependency.getUsages().stream()
                .map(DtoConverter::toBaseDto)
                .sorted(DtoConverter::alphabetic)
                .collect(Collectors.toList());
        return dto;
    }

    private static int alphabetic(ProjectService.DependencyDto l, ProjectService.DependencyDto r) {
        return l.title.compareToIgnoreCase(r.title);
    }

    public static ProjectService.DependencyDto toBaseDto(Dependency dependency) {
        final var dto = new ProjectService.DependencyDto();
        dto.reference = referenceTo(dependency);
        dto.title = dependency.getTitle();
        dto.version = dependency.getVersion();
        dto.license = dependency.getLicense();
        dto.issues = dependency.getIssueCount();
        return dto;
    }

    static ProjectService.DependencyDto toDto(Relation relation) {
        final var dto = toBaseDto(relation.getTarget());
        dto.relation = relation.getType().name().toLowerCase();
        return dto;
    }

    static @NullOr String referenceTo(Dependency dependency) {
        return dependency.getPackage()
                .map(pkg -> pkg.getReference() + "@" + dependency.getVersion())
                .orElse(null);
    }
}

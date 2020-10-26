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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

abstract class DtoConverter {
    static ProjectService.ProjectDto toDto(Project project) {
        final var dto = toBaseDto(project);
        dto.packages = project.getRootDependencies().stream()
                .map(dep -> DtoConverter.toNestedDto(dep, new HashSet<>()))
                .sorted((l, r) -> l.title.compareToIgnoreCase(r.title))
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
        final var dto = toNestedDto(dependency, new HashSet<>());
        dto.violations = violations.stream().map(LicenseViolation::getMessage).collect(Collectors.toList());
        return dto;
    }

    private static ProjectService.DependencyDto toNestedDto(Dependency dependency, Set<Dependency> visited) {
        final ProjectService.DependencyDto dto = toBaseDto(dependency);
        final var nextVisited = new HashSet<>(visited);
        nextVisited.add(dependency);
        dto.dependencies = dependency.getRelations().stream()
                .filter(relation -> !visited.contains(relation.getTarget()))
                .map(relation -> toDto(relation, nextVisited))
                .sorted((l, r) -> l.title.compareToIgnoreCase(r.title))
                .collect(Collectors.toList());
        return dto;
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

    static ProjectService.DependencyDto toDto(Relation relation, Set<Dependency> visited) {
        final var dto = toNestedDto(relation.getTarget(), visited);
        dto.relation = relation.getType().name().toLowerCase();
        return dto;
    }

    static @NullOr String referenceTo(Dependency dependency) {
        return dependency.getPackage()
                .map(pkg -> pkg.getReference() + "@" + dependency.getVersion())
                .orElse(null);
    }

}

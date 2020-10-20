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
    static ProjectService.ProjectDto toDto(Project project, List<LicenseViolation> violations) {
        final var dto = toBaseDto(project);
        dto.violations = violations.stream().map(DtoConverter::toDto).collect(Collectors.toList());
        dto.packages = project.getRootDependencies().stream()
                .map(dep -> DtoConverter.toBaseDto(dep, new HashSet<>()))
                .sorted((l, r) -> l.title.compareToIgnoreCase(r.title))
                .collect(Collectors.toList());
        return dto;
    }

    static ProjectService.ProjectDto toBaseDto(Project project) {
        final var dto = new ProjectService.ProjectDto();
        dto.id = project.getId();
        dto.title = project.getTitle();
        dto.issues = project.getIssueCount();
        return dto;
    }

    static ProjectService.DependencyDto toDto(Dependency dependency) {
        // Add once there are more elaborate details
        return toBaseDto(dependency, new HashSet<>());
    }

    private static ProjectService.DependencyDto toBaseDto(Dependency dependency, Set<Dependency> visited) {
        final var dto = new ProjectService.DependencyDto();
        dto.reference = referenceTo(dependency);
        dto.title = dependency.getTitle();
        dto.version = dependency.getVersion();
        dto.license = dependency.getLicense();
        dto.issues = dependency.getIssueCount();
        final var nextVisited = new HashSet<>(visited);
        nextVisited.add(dependency);
        dto.dependencies = dependency.getRelations().stream()
                .filter(relation -> !visited.contains(relation.getTarget()))
                .map(relation -> toDto(relation, nextVisited))
                .sorted((l, r) -> l.title.compareToIgnoreCase(r.title))
                .collect(Collectors.toList());
        return dto;
    }

    static ProjectService.DependencyDto toDto(Relation relation, Set<Dependency> visited) {
        final var dto = toBaseDto(relation.getTarget(), visited);
        dto.relation = relation.getType().name().toLowerCase();
        return dto;
    }

    static ProjectService.ViolationDto toDto(LicenseViolation violation) {
        final var dto = new ProjectService.ViolationDto();
        dto.reference = referenceTo(violation.getDependency());
        dto.dependency = violation.getDependency().getTitle();
        dto.violation = violation.getMessage();
        return dto;
    }

    static @NullOr String referenceTo(Dependency dependency) {
        return dependency.getPackage()
                .map(pkg -> pkg.getReference() + "@" + dependency.getVersion())
                .orElse(null);
    }

}

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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

abstract class DtoConverter {
    static ProjectService.ProjectDto toDto(Project project) {
        final var dto = toBaseDto(project);
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
        dependency.getPackage().ifPresent(pkg -> dto.reference = pkg.getReference() + "@" + dependency.getVersion());
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
}

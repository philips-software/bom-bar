package com.philips.research.collector.core.domain;

import com.philips.research.collector.core.ProjectService;

import java.util.List;
import java.util.stream.Collectors;

abstract class DtoConverter {
    static ProjectService.ProjectDto toDto(Project project) {
        final var dto = new ProjectService.ProjectDto();

        dto.id = project.getId();
        dto.title = project.getTitle();
        dto.packages = toDtoList(project.getPackages());

        return dto;
    }

    static ProjectService.PackageDto toDto(Package pkg) {
        final var dto = new ProjectService.PackageDto();

        dto.license = pkg.getLicense();
        dto.title = pkg.getName();
        dto.reference = pkg.getName();
        dto.children = toDtoList(pkg.getChildren());
//        dto.relation = pkg.getRelation().toString();

        return dto;
    }

    static List<ProjectService.PackageDto> toDtoList(List<Package> packages) {
        return packages.stream().map(DtoConverter::toDto).collect(Collectors.toList());
    }
}

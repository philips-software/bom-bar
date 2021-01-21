/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain;

import com.philips.research.bombar.core.PackageService;
import com.philips.research.bombar.core.ProjectService;
import com.philips.research.bombar.core.domain.licenses.LicenseViolation;

import java.util.Collections;
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
        final var dto = new ProjectService.ProjectDto(project.getId());
        dto.title = project.getTitle();
        dto.updated = project.getLastUpdate().orElse(null);
        dto.distribution = project.getDistribution().name();
        dto.phase = project.getPhase().name();
        dto.issues = project.getIssueCount();
        return dto;
    }

    static ProjectService.DependencyDto toDto(Dependency dependency, List<LicenseViolation> violations) {
        final var dto = toBaseDto(dependency);
        dependency.getPackage().ifPresent(pkg -> dto.pkg = toDto(pkg));
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

    static ProjectService.DependencyDto toDto(Relation relation) {
        final var dto = toBaseDto(relation.getTarget());
        dto.relation = relation.getType().name().toLowerCase();
        return dto;
    }

    public static ProjectService.DependencyDto toBaseDto(Dependency dependency) {
        final var dto = new ProjectService.DependencyDto(dependency.getKey());
        dependency.getPackageUrl().ifPresent(purl -> dto.purl = purl);
        dto.title = dependency.getTitle();
        dto.version = dependency.getVersion();
        dto.license = dependency.getLicense();
        dto.issues = dependency.getIssueCount();
        dependency.getExemption().ifPresent(rationale -> dto.exemption = rationale);
        return dto;
    }

    private static int alphabetic(ProjectService.DependencyDto l, ProjectService.DependencyDto r) {
        return l.title.compareToIgnoreCase(r.title);
    }

    public static PackageService.PackageDto toDto(Package pkg) {
        final var dto = new PackageService.PackageDto();
        dto.reference = pkg.getReference();
        dto.name = pkg.getName();
        dto.approval = approvalOf(pkg);
        pkg.getVendor().ifPresent(vendor -> dto.vendor = vendor);
        pkg.getHomepage().ifPresent(url -> dto.homepage = url);
        pkg.getDescription().ifPresent(description -> dto.description = description);
        dto.licenseExemptions.addAll(pkg.getLicenseExemptions());
        Collections.sort(dto.licenseExemptions);
        return dto;
    }

    private static PackageService.Approval approvalOf(Package pkg) {
        switch (pkg.getAcceptance()) {
            case APPROVED:
                return PackageService.Approval.APPROVED;
            case FORBIDDEN:
                return PackageService.Approval.REJECTED;
            case PER_PROJECT:
                return PackageService.Approval.NEEDS_APPROVAL;
            case NOT_A_PACKAGE:
                return PackageService.Approval.NOT_A_PACKAGE;
            case DEFAULT:
            default:
                return PackageService.Approval.CONTEXT;
        }

    }
}

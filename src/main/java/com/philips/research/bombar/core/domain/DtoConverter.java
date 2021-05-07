/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain;

import com.philips.research.bombar.core.PackageService;
import com.philips.research.bombar.core.PackageService.PackageDto;
import com.philips.research.bombar.core.ProjectService.DependencyDto;
import com.philips.research.bombar.core.ProjectService.ProjectDto;
import com.philips.research.bombar.core.domain.licenses.LicenseViolation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

abstract class DtoConverter {
    static ProjectDto toDto(Project project) {
        final var dto = toBaseDto(project);
        dto.packages = project.getDependencies().stream()
                .map(DtoConverter::toBaseDto)
                .sorted(DtoConverter::alphabetic)
                .collect(Collectors.toList());
        return dto;
    }

    static ProjectDto toBaseDto(Project project) {
        final var dto = new ProjectDto(project.getId());
        dto.title = project.getTitle();
        dto.updated = project.getLastUpdate().orElse(null);
        dto.distribution = project.getDistribution().name();
        dto.phase = project.getPhase().name();
        dto.issues = project.getIssueCount();
        return dto;
    }

    static DependencyDto toDto(Dependency dependency, List<LicenseViolation> violations) {
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
        dependency.getExemption().ifPresent(reason -> dto.exemption = reason);
        return dto;
    }

    static DependencyDto toDto(Relation relation) {
        final var dto = toBaseDto(relation.getTarget());
        dto.relation = relation.getType().name().toLowerCase();
        return dto;
    }

    public static DependencyDto toBaseDto(Dependency dependency) {
        final var dto = new DependencyDto(dependency.getKey());
        dependency.getPurl().ifPresent(purl -> dto.purl = purl);
        dto.title = dependency.getTitle();
        dto.version = dependency.getVersion();
        dto.license = dependency.getLicense();
        dto.issues = dependency.getIssueCount();
        dto.isRoot = dependency.isRoot();
        dto.isDevelopment = dependency.isDevelopment();
        dto.isDelivered = dependency.isDelivered();
        dependency.getExemption().ifPresent(rationale -> dto.exemption = rationale);
        return dto;
    }

    private static int alphabetic(DependencyDto l, DependencyDto r) {
        return l.title.compareToIgnoreCase(r.title);
    }

    public static PackageDto toDto(Package pkg) {
        final var dto = new PackageDto();
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

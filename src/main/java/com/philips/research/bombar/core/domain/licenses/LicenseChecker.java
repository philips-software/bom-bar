/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.bombar.core.domain.licenses;

import com.philips.research.bombar.core.domain.Dependency;
import com.philips.research.bombar.core.domain.Package;
import com.philips.research.bombar.core.domain.Project;
import com.philips.research.bombar.core.domain.Relation;

import java.util.*;
import java.util.stream.Collectors;

public class LicenseChecker {
    private final LicenseRegistry registry;
    private final Project project;
    private final Map<Dependency, List<LicenseType>> licenseCache = new HashMap<>();
    private final List<LicenseViolation> violations = new ArrayList<>();

    public LicenseChecker(LicenseRegistry registry, Project project) {
        this.registry = registry;
        this.project = project;
    }

    public List<LicenseViolation> violations() {
        clearCaches();
        project.getDependencies().forEach(this::verify);
        return violations;
    }

    public List<LicenseViolation> violations(Dependency dependency) {
        clearCaches();
        verify(dependency);
        return violations.stream()
                .filter(v -> v.getDependency() == dependency)
                .collect(Collectors.toList());
    }

    private void clearCaches() {
        licenseCache.clear();
        violations.clear();
    }

    private void verify(Dependency dependency) {
        final var override = checkPackage(dependency);
        if (!override) {
            checkLicense(dependency);
        }
        dependency.getRelations()
                .forEach(relation -> checkRelation(dependency, relation));
        dependency.setIssueCount((int) violations.stream()
                .filter(v -> v.getDependency().equals(dependency))
                .count());
    }

    private boolean checkPackage(Dependency dependency) {
        return dependency.getPackage()
                .flatMap(pkg -> Optional.of(checkPackageDefinition(pkg, dependency)))
                .orElse(false);
    }

    /**
     * @return true if package overrides license violations
     */
    private boolean checkPackageDefinition(Package pkg, Dependency dependency) {
        switch (pkg.getAcceptance()) {
            case NOT_A_PACKAGE:
                violations.add(new LicenseViolation(dependency, "is not a package"));
                break;
            case FORBIDDEN:
                violations.add(new LicenseViolation(dependency, "is forbidden for use in any project"));
                break;
            case PER_PROJECT:
                if (dependency.getExemption().isEmpty()) {
                    violations.add(new LicenseViolation(dependency, "requires per-project exemption"));
                }
                break;
            case APPROVED:
                return true;
            case DEFAULT:
            default:
                // Ignore
        }
        return false;
    }

    private void checkLicense(Dependency dependency) {
        final var licenses = dependency.getLicense();
        if (licenses.isBlank()) {
            if (!isLicenseExempted(dependency, "")) {
                violations.add(new LicenseViolation(dependency, "has no license"));
            }
        } else if (!isLicenseCompatible(dependency)) {
            if (licenses.toLowerCase().contains(" or ")) {
                violations.add(new LicenseViolation(dependency, String.format("has alternative licenses '%s'", licenses)));
            } else {
                violations.add(new LicenseViolation(dependency, String.format("has incompatible licenses '%s'", licensesOf(dependency))));
            }
        }
    }

    private boolean isLicenseCompatible(Dependency dependency) {
        var licenses = licensesOf(dependency);

        return (licenses.size() <= 1) || licenses.stream()
                .anyMatch(lic -> licenses.stream()
                        .allMatch(l -> l == lic ||
                                lic.unmetDemands(l, project.getDistribution(), Relation.Relationship.values()[0]).isEmpty()));
    }

    private void checkRelation(Dependency dependency, Relation relation) {
        // Aggregates all accepted demands into a single temporary license
        final var all = new LicenseType("");
        licensesOf(dependency).stream()
                .flatMap(l -> l.accepts().stream())
                .forEach(all::accept);

        licensesOf(relation.getTarget()).stream()
                .flatMap(license -> all.unmetDemands(license, project.getDistribution(), relation.getType()).stream())
                .forEach(term -> {
                    var message = "depends on incompatible " + term.getDescription()
                            + " of package " + relation.getTarget();
                    if (relation.getTarget().getLicense().toLowerCase().contains(" or ")) {
                        message += " that might require an explicit choice between license alternatives";
                    }
                    violations.add(new LicenseViolation(dependency, message));
                });
    }

    private List<LicenseType> licensesOf(Dependency dependency) {
        return licenseCache.computeIfAbsent(dependency, (dep) ->
                dep.getLicenses().stream()
                        .map((s) -> {
                            try {
                                return Optional.of(registry.licenseType(s));
                            } catch (IllegalArgumentException e) {
                                if (!isLicenseExempted(dependency, s)) {
                                    violations.add(new LicenseViolation(dependency, String.format("has unknown license '%s'", s)));
                                }
                                return Optional.<LicenseType>empty();
                            }
                        })
                        .flatMap(Optional::stream)
                        .collect(Collectors.toList()));
    }

    private boolean isLicenseExempted(Dependency dependency, String license) {
        return dependency.getExemption().isPresent() || dependency.getPackage()
                .filter(pkg -> pkg.isLicenseExempted(license))
                .isPresent();
    }
}

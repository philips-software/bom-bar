/*
 * This software and associated documentation files are
 *
 * Copyright © 2020-2020 Koninklijke Philips N.V.
 *
 * and is made available for use within Philips and/or within Philips products.
 *
 * All Rights Reserved
 */

package com.philips.research.bombar.core.domain.licenses;

import com.philips.research.bombar.core.domain.Dependency;
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

    public List<LicenseViolation> verify() {
        violations.clear();
        project.getDependencies().forEach(this::verify);
        return violations;
    }

    private void verify(Dependency dependency) {
        checkLicense(dependency);
        dependency.getRelations()
                .forEach(relation -> checkRelation(dependency, relation));
    }

    private void checkLicense(Dependency dependency) {
        final var licenses = dependency.getLicense();
        if (licenses.isBlank()) {
            violations.add(new LicenseViolation(dependency, "has no license"));
        } else if (licenses.toLowerCase().contains(" or ")) {
            violations.add(new LicenseViolation(dependency, String.format("has alternative licenses '%s'", licenses)));
        } else if (!isLicenseCompatible(dependency)) {
            violations.add(new LicenseViolation(dependency, String.format("has incompatible licenses '%s'", licensesOf(dependency))));
        }
    }

    private boolean isLicenseCompatible(Dependency dependency) {
        var licenses = licensesOf(dependency);

        return (licenses.size() <= 1) || licenses.stream()
                .anyMatch(lic -> licenses.stream()
                        .allMatch(l -> l == lic ||
                                lic.incompatibilities(l, project.getDistribution(), Relation.Type.MODIFIED_CODE).isEmpty()));
    }

    private void checkRelation(Dependency dependency, Relation relation) {
        final var dummy = new LicenseType("");
        licensesOf(dependency).stream()
                .flatMap(l -> l.accepts().stream())
                .forEach(dummy::accept);

        licensesOf(relation.getTarget()).stream()
                .flatMap(l -> dummy.incompatibilities(l, project.getDistribution(), relation.getType()).stream())
                .forEach(term -> violations.add(new LicenseViolation(dependency, "depends on incompatible "
                        + term.getDescription() + " of package " + relation.getTarget())));
    }

    private List<LicenseType> licensesOf(Dependency dependency) {
        return licenseCache.computeIfAbsent(dependency, (dep) ->
                split(dep.getLicense()).stream()
                        .map((s) -> {
                            try {
                                return Optional.of(registry.licenseType(s));
                            } catch (IllegalArgumentException e) {
                                violations.add(new LicenseViolation(dependency, String.format("has unknown license '%s'", s)));
                                return Optional.<LicenseType>empty();
                            }
                        })
                        .flatMap(Optional::stream)
                        .collect(Collectors.toList()));
    }

    private List<String> split(String license) {
        return Arrays.stream(license
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .split("\\s+(AND|and|OR|or)\\s+"))
                .filter(l -> !l.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }
}

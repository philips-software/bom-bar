/*
 * Copyright (c) 2020-2020, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

package com.philips.research.collector.core.domain.licenses;

import com.philips.research.collector.core.domain.Dependency;
import com.philips.research.collector.core.domain.Project;
import com.philips.research.collector.core.domain.Relation;

import java.util.*;

public class LicenseChecker {
    private final LicenseRegistry registry;
    private final Project project;
    private final Map<Dependency, Aggregate> cache = new HashMap<>();
    private final List<LicenseViolation> violations = new ArrayList<>();

    public LicenseChecker(LicenseRegistry registry, Project project) {
        this.registry = registry;
        this.project = project;
    }

    public List<LicenseViolation> verify() {
        violations.clear();
        project.getDependencies().forEach(this::termsFromCache);
        return violations;
    }

    private Aggregate termsFromCache(Dependency dependency) {
        var terms = cache.get(dependency);
        if (terms == null) {
            terms = termsFor(dependency);
            cache.put(dependency, terms);
        }
        return terms;
    }

    private Aggregate termsFor(Dependency dependency) {
        final var terms = new Aggregate();

        final var licenses = dependency.getLicense();
        if (licenses.isBlank()) {
            violations.add(new LicenseViolation(dependency, "has no license"));
        } else if (licenses.toLowerCase().contains(" or ")) {
            violations.add(new LicenseViolation(dependency, String.format("has alternative licenses '%s'", licenses)));
        }

        checkDependency(dependency, terms);

        return terms;
    }

    private void checkDependency(Dependency dependency, Aggregate terms) {
        for (var license : split(dependency.getLicense())) {
            try {
                final var type = registry.licenseType(license);
                for (Relation relation : dependency.getRelations()) {
                    checkRelation(dependency, type, relation, terms);
                }
            } catch (IllegalArgumentException e) {
                violations.add(new LicenseViolation(dependency, String.format("has unknown license '%s'", license)));
            }
        }

        if (terms.hasViolation()) {
            violations.add(new LicenseViolation(dependency, "has a conflict in its subpackages"));
        }
    }

    private void checkRelation(Dependency dependency, LicenseType type, Relation relation, Aggregate terms) {
        var isViolating = false;

        for (var childLicense : split(relation.getTarget().getLicense())) {
            try {
                final var childType = registry.licenseType(childLicense);
                if (!type.conflicts(childType, project.getDistribution(), relation.getType()).isEmpty()) {
                    violations.add(new LicenseViolation(dependency,
                            String.format("license '%s' is not compatible with license '%s' of package %s",
                                    type, childType, relation.getTarget())));
                    isViolating = true;
                } else {
                    terms.add(childType, project.getDistribution(), relation.getType());
                }
            } catch (IllegalArgumentException e) {
                // Ignore because unknown licenses are caught at top level
            }
        }

        if (!isViolating) {
            terms.add(termsFromCache(relation.getTarget()));
        }
    }

    private String[] split(String license) {
        return Arrays.stream(license
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .split("\\s+(AND|and|OR|or)\\s+"))
                .filter(l -> !l.isBlank())
                .distinct()
                .toArray(String[]::new);
    }

    private static class Aggregate {
        private final Set<Term> required = new HashSet<>();
        private final Set<Term> forbidden = new HashSet<>();

        void add(LicenseType type, Enum<?>... conditions) {
            required.addAll(type.requiredGiven(conditions));
            forbidden.addAll(type.forbiddenGiven(conditions));
        }

        void add(Aggregate aggregate) {
            required.addAll(aggregate.required);
            forbidden.addAll(aggregate.forbidden);
        }

        boolean hasViolation() {
            final var intersection = new HashSet<>(forbidden);
            intersection.retainAll(required);
            return !intersection.isEmpty();
        }
    }
}
